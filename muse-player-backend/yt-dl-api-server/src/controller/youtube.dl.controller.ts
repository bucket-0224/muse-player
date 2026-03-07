import express, { NextFunction } from 'express';
import youtube_dl, { Payload } from 'youtube-dl-exec';
import path from 'path';
import fs from 'fs';
import os from 'os';
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
                format: 'bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best',
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
            // 403 발생 시 캐시 파괴 및 재시도 유도
            if (proxyRes.statusCode === 403) {
                videoUrlCache.delete(videoId);
                return res.status(410).json({ message: 'Link expired, please retry' });
            }

            // 필수 헤더 전달 (브라우저 플레이어 호환성)
            res.status(proxyRes.statusCode || 200);

            const forwardHeaders = [
                'content-type',
                'content-length',
                'content-range',
                'accept-ranges',
                'cache-control',
            ];

            forwardHeaders.forEach((h) => {
                if (proxyRes.headers[h]) res.setHeader(h, proxyRes.headers[h]!);
            });

            // iOS 및 모바일 브라우저 최적화 헤더
            res.setHeader('Access-Control-Allow-Origin', '*');
            res.setHeader('X-Content-Type-Options', 'nosniff');

            // 4️⃣ [효율적 파이핑]
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
                format: 'bestaudio[ext=m4a]/bestaudio', // iOS 호환성을 위한 m4a 우선 순위
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
            // 403 발생 시 캐시 파괴 및 재시도 유도
            if (proxyRes.statusCode === 403) {
                videoUrlCache.delete(videoId);
                return res.status(410).json({ message: 'Link expired, please retry' });
            }

            // 필수 헤더 전달 (브라우저 플레이어 호환성)
            res.status(proxyRes.statusCode || 200);

            const forwardHeaders = [
                'content-type',
                'content-length',
                'content-range',
                'accept-ranges',
                'cache-control',
            ];

            forwardHeaders.forEach((h) => {
                if (proxyRes.headers[h]) res.setHeader(h, proxyRes.headers[h]!);
            });

            // iOS 및 모바일 브라우저 최적화 헤더
            res.setHeader('Access-Control-Allow-Origin', '*');
            res.setHeader('X-Content-Type-Options', 'nosniff');

            // 4️⃣ [효율적 파이핑]
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
        return res.status(400).json({ message: 'Missing cookie file or videoId' });
    }

    const videoGetByYoutubeWatchId: any = await youtube_dl(`https://www.youtube.com/watch?v=${videoId}`, {
        dumpSingleJson: true,
        noCheckCertificates: true,
        noWarnings: true,
        skipDownload: true,
        preferFreeFormats: true,
        addHeader: ['referer:youtube.com', 'user-agent:googlebot'],
    });

    console.log(`video : ${JSON.stringify(videoGetByYoutubeWatchId)}`);

    const urlList = videoGetByYoutubeWatchId['formats'].filter((item: any) => item['asr'] != undefined);

    console.log(`urlList : ${urlList}`);

    return res.status(200).json({
        title: videoGetByYoutubeWatchId['title'],
        artist: videoGetByYoutubeWatchId['artist'] ?? videoGetByYoutubeWatchId['uploader'],
        thumbnail: videoGetByYoutubeWatchId['thumbnail'],
        videoId: videoId,
        fullJson: videoGetByYoutubeWatchId,
        message: 'youtube link generated.',
    });
}

export default {
    getVideoFeaturesByVideoId,
    getStreamVideoFeaturesVideo,
    getShortsVideo,
    getStreamShortFeaturesVideo,
    uploadCookiesForStreamVideo,
};
