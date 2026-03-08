import express, { NextFunction } from 'express';
import youtube_dl, { Payload } from 'youtube-dl-exec';
import path from 'path';
import fs from 'fs';
import os from 'os';
import * as yt from 'youtube-info-streams';
import { v4 as uuidv4 } from 'uuid';
import YouTube from 'youtube-sr';
import { promisify } from 'util';
import fetch from 'node-fetch';
import { spawn } from 'child_process';
import getAudioDuration from '../utils/duration.util';
import waitForFile from '../utils/\bwait.file.util';

import https from 'https';
import { LRUCache } from 'lru-cache';

import ytdl from '@distube/ytdl-core'; // Python youtube-dl 대체 (훨씬 빠름)
import { request } from 'https';
import youtubeDl from 'youtube-dl-exec';

// 1️⃣ [캐시 설정] URL 유효기간(약 6시간) 동안 재사용
const videoUrlCache = new Map<string, CacheEntry>();
const CACHE_DURATION = 1000 * 60 * 60; // 1시간 캐시

interface CacheEntry {
    url: string;
    expiresAt: number;
}

async function uploadCookiesForStreamVideo(req: express.Request, res: express.Response, next: express.NextFunction) {
    if (!req.file) return res.status(400).json({ message: 'No cookie file' });

    const cookiePath = path.join(process.cwd(), 'src/controller/cookies', 'cookies.txt');
    fs.writeFileSync(cookiePath, req.file.buffer); // 서버에 항상 저장

    return res.status(200).json({ message: 'Cookie uploaded' });
}

async function getShortsVideo(req: express.Request, res: express.Response) {
    const { keyword } = req.query;

    if (!keyword || typeof keyword !== 'string') {
        return res.status(400).json({ message: 'keyword required' });
    }

    const videos = await YouTube.search(keyword as string, {
        type: 'video',
        limit: 20,
    });

    return res.status(200).json({
        videoIds: videos.map((video) => video.id ?? ''),
    });
}

async function getStreamShortFeaturesVideo(req: express.Request, res: express.Response) {
    const { videoId } = req.query;

    if (!videoId || typeof videoId !== 'string') {
        return res.status(400).json({ message: 'videoId required' });
    }

    try {
        const now = Date.now();
        const cached = videoUrlCache.get(videoId);
        let audioUrl: string;

        // 1️⃣ [속도 최적화] 유효 캐시 즉시 반환
        if (cached && cached.expiresAt > now) {
            audioUrl = cached.url;
        } else {
            // 2️⃣ [성능 최적화] yt-dlp 호출 시 불필요한 메타데이터 로드 방지
            // --get-url 옵션을 사용하여 오직 URL만 빠르게 추출합니다.
            const output = await youtubeDl(`https://www.youtube.com/watch?v=${videoId}`, {
                getUrl: true,
                format: 'best[ext=mp4]/best',
                noWarnings: true,
                noCheckCertificates: true,
            });

            audioUrl = typeof output === 'string' ? output.trim() : (output as any).stdout.trim();

            if (!audioUrl) throw new Error('Failed to extract URL');

            videoUrlCache.set(videoId, { url: audioUrl, expiresAt: now + CACHE_DURATION });
        }

        // 3️⃣ [스트리밍/Range 처리]
        const range = req.headers.range;
        const urlObj = new URL(audioUrl);

        const options: https.RequestOptions = {
            hostname: urlObj.hostname,
            path: `${urlObj.pathname}${urlObj.search}`,
            method: 'GET',
            headers: {
                'User-Agent':
                    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                Range: range || 'bytes=0-', // Range가 없더라도 초기 바이트 요청
                Connection: 'keep-alive',
            },
        };

        const proxyReq = https.request(options, (proxyRes) => {
            if (proxyRes.statusCode === 403) {
                videoUrlCache.delete(videoId);
                return res.status(410).json({ message: 'Link expired' });
            }

            // [핵심] 206 Partial Content 대응 (스트리밍의 핵심)
            // 클라이언트가 Range 요청을 보냈다면 206을 반환해야 합니다.
            const statusCode = proxyRes.statusCode || 200;
            res.status(statusCode);

            // 헤더 복사 시 'transfer-encoding'은 제거 (Node.js가 알아서 처리하게 함)
            Object.keys(proxyRes.headers).forEach((h) => {
                const headerValue = proxyRes.headers[h];

                // headerValue가 undefined가 아닐 때만 셋팅
                if (h.toLowerCase() !== 'transfer-encoding' && headerValue !== undefined) {
                    res.setHeader(h, headerValue);
                }
            });

            // Content-Type 강제 지정 (MP4 스트리밍임을 명시)
            if (!proxyRes.headers['content-type']) {
                res.setHeader('Content-Type', 'video/mp4');
            }

            proxyRes.pipe(res);
        });

        proxyReq.on('error', (err) => {
            console.error('Proxy request error:', err);
            if (!res.headersSent) res.status(500).end();
        });

        // 클라이언트가 연결을 끊으면 업스트림 요청도 즉시 중단 (리소스 절약)
        req.on('close', () => {
            proxyReq.destroy();
        });

        proxyReq.end();
    } catch (error) {
        console.error('Final Handler Error:', error);
        if (!res.headersSent) res.status(500).json({ message: 'Internal Server Error' });
    }
}

async function getStreamVideoFeaturesVideo(req: express.Request, res: express.Response) {
    const { videoId } = req.query;

    if (!videoId || typeof videoId !== 'string') {
        return res.status(400).json({ message: 'videoId required' });
    }

    try {
        const now = Date.now();
        const cached = videoUrlCache.get(videoId);
        let audioUrl: string;

        // 1️⃣ [속도 최적화] 유효 캐시 즉시 반환
        if (cached && cached.expiresAt > now) {
            audioUrl = cached.url;
        } else {
            // 2️⃣ [성능 최적화] yt-dlp 호출 시 불필요한 메타데이터 로드 방지
            // --get-url 옵션을 사용하여 오직 URL만 빠르게 추출합니다.
            const output = await youtubeDl(`https://www.youtube.com/watch?v=${videoId}`, {
                getUrl: true,
                // [중요] mp4 포맷으로 고정해야 브라우저나 모바일 앱이 '파일 끝'을 안 기다리고 바로 재생합니다.
                format: 'best[ext=mp4]/best',
                noWarnings: true,
                noCheckCertificates: true,
            });

            audioUrl = typeof output === 'string' ? output.trim() : (output as any).stdout.trim();

            if (!audioUrl) throw new Error('Failed to extract URL');

            videoUrlCache.set(videoId, { url: audioUrl, expiresAt: now + CACHE_DURATION });
        }

        // 3️⃣ [스트리밍/Range 처리]
        const range = req.headers.range;
        const urlObj = new URL(audioUrl);

        const options: https.RequestOptions = {
            hostname: urlObj.hostname,
            path: `${urlObj.pathname}${urlObj.search}`,
            method: 'GET',
            headers: {
                'User-Agent':
                    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                Range: range || 'bytes=0-', // Range가 없더라도 초기 바이트 요청
                Connection: 'keep-alive',
            },
        };

        const proxyReq = https.request(options, (proxyRes) => {
            if (proxyRes.statusCode === 403) {
                videoUrlCache.delete(videoId);
                return res.status(410).json({ message: 'Link expired' });
            }

            // [핵심] 206 Partial Content 대응 (스트리밍의 핵심)
            // 클라이언트가 Range 요청을 보냈다면 206을 반환해야 합니다.
            const statusCode = proxyRes.statusCode || 200;
            res.status(statusCode);

            // 헤더 복사 시 'transfer-encoding'은 제거 (Node.js가 알아서 처리하게 함)
            Object.keys(proxyRes.headers).forEach((h) => {
                const headerValue = proxyRes.headers[h];

                // headerValue가 undefined가 아닐 때만 셋팅
                if (h.toLowerCase() !== 'transfer-encoding' && headerValue !== undefined) {
                    res.setHeader(h, headerValue);
                }
            });

            // Content-Type 강제 지정 (MP4 스트리밍임을 명시)
            if (!proxyRes.headers['content-type']) {
                res.setHeader('Content-Type', 'video/mp4');
            }

            proxyRes.pipe(res);
        });

        proxyReq.on('error', (err) => {
            console.error('Proxy request error:', err);
            if (!res.headersSent) res.status(500).end();
        });

        // 클라이언트가 연결을 끊으면 업스트림 요청도 즉시 중단 (리소스 절약)
        req.on('close', () => {
            proxyReq.destroy();
        });

        proxyReq.end();
    } catch (error) {
        console.error('Final Handler Error:', error);
        if (!res.headersSent) res.status(500).json({ message: 'Internal Server Error' });
    }
}

async function getVideoFeaturesByVideoId(req: express.Request, res: express.Response, next: express.NextFunction) {
    const { videoId } = req.query;

    if (!videoId) {
        return res.status(400).json({ message: 'Missing videoId' });
    }

    try {
        // youtube-dl 대신 유튜브 공식 oEmbed API 호출 (속도 압도적)
        const response = await fetch(
            `https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=${videoId}&format=json`
        );

        if (!response.ok) throw new Error('Video not found');

        const data = await response.json();

        console.log('response : ' + JSON.stringify(data));

        return res.status(200).json({
            title: data.title,
            artist: data.author_name, // oEmbed는 채널명을 author_name으로 줍니다.
            thumbnail: data.thumbnail_url,
            videoId: videoId,
            message: 'Metadata fetched instantly.',
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: 'Failed to fetch metadata' });
    }
}

export default {
    getVideoFeaturesByVideoId,
    getStreamVideoFeaturesVideo,
    getShortsVideo,
    getStreamShortFeaturesVideo,
    uploadCookiesForStreamVideo,
};
