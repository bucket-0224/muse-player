import express, { NextFunction } from 'express';
import youtube_dl, { Payload } from 'youtube-dl-exec';
import path from 'path';
import fs from 'fs';
import os from 'os';
import { v4 as uuidv4 } from 'uuid';
import { pipeline } from 'stream';
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

const ytdlAgent = ytdl.createAgent();
const proxyAgent = new https.Agent({
    keepAlive: true,
    keepAliveMsecs: 10000,
    maxSockets: Infinity,
});

async function uploadCookiesForStreamVideo(req: express.Request, res: express.Response, next: express.NextFunction) {
    if (!req.file) return res.status(400).json({ message: 'No cookie file' });

    const cookiePath = path.join(process.cwd(), 'src/controller/cookies', 'cookies.txt');
    fs.writeFileSync(cookiePath, req.file.buffer); // 서버에 항상 저장

    return res.status(200).json({ message: 'Cookie uploaded' });
}

async function getStreamVideoFeatruesVideo(req: express.Request, res: express.Response) {
    const { videoId } = req.query;

    if (!videoId || typeof videoId !== 'string') {
        return res.status(400).json({ message: 'videoId required' });
    }

    try {
        let audioUrl: string | undefined;
        const now = Date.now();
        const cached = videoUrlCache.get(videoId);

        // 2️⃣ [최적화] 캐시 확인 및 만료 체크
        if (cached && cached.expiresAt > now) {
            audioUrl = cached.url;
        } else {
            // 캐시가 없거나 만료됨 -> 새로 추출
            try {
                // yt-dlp(Python) 대신 Node.js 네이티브 라이브러리 사용 (속도: 3s -> 0.3s)
                const info = await ytdl.getInfo(`https://www.youtube.com/watch?v=${videoId}`, {
                    agent: ytdlAgent, // 에이전트 주입
                });

                // 오디오 포맷 중 'audio/mp4' (m4a)에 가장 적합한 포맷 필터링
                const format = ytdl.chooseFormat(info.formats, {
                    quality: 'highestaudio',
                    filter: 'audioonly',
                });

                if (format && format.url) {
                    audioUrl = format.url;
                    // 유튜브 URL은 보통 6시간 뒤 만료되므로 1시간만 캐시
                    videoUrlCache.set(videoId, { url: audioUrl, expiresAt: now + CACHE_DURATION });
                }
            } catch (e) {
                console.error('Info extraction error:', e);
                return res.status(500).json({ message: 'Failed to extract URL' });
            }
        }

        if (!audioUrl) {
            return res.status(500).json({ message: 'Audio URL not found' });
        }

        // 3️⃣ [스트리밍] Range 헤더 처리 및 프록시
        const range = req.headers.range;
        const options: https.RequestOptions = {
            headers: {
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) ...', // 기존 유지
            },
            agent: proxyAgent, // 연결 재사용
        };

        if (range && options.headers) {
            options.headers['Range'] = range;
        }

        const proxyReq = https.request(audioUrl, options, (proxyRes) => {
            // 403 발생 시 캐시 즉시 삭제 및 재시도 로직(선택적) 또는 에러 리턴
            if (proxyRes.statusCode === 403) {
                videoUrlCache.delete(videoId);
                if (!res.headersSent) return res.status(403).send('Link Expired');
                return;
            }

            const statusCode = proxyRes.statusCode || 200;

            // 헤더 복사
            const headersToForward = [
                'content-type',
                'content-length',
                'content-range',
                'accept-ranges',
                'content-disposition',
                'date',
                'last-modified',
            ];

            headersToForward.forEach((key) => {
                if (proxyRes.headers[key]) res.setHeader(key, proxyRes.headers[key]!);
            });

            // iOS/Safari 호환성 강제 설정
            res.setHeader('Content-Type', 'audio/mp4');
            res.setHeader('Access-Control-Allow-Origin', '*');
            res.setHeader('Connection', 'keep-alive'); // 클라이언트와도 연결 유지

            res.writeHead(statusCode);
            proxyRes.pipe(res);
        });

        proxyReq.on('error', (e) => {
            console.error('Proxy Error:', e);
            if (!res.headersSent) res.end();
        });

        proxyReq.end();

        req.on('close', () => {
            if (!proxyReq.destroyed) proxyReq.destroy();
        });
    } catch (error) {
        console.error('Handler Error:', error);
        if (!res.headersSent) res.status(500).json({ message: 'Internal Server Error' });
    }
}

async function getVideoFeaturesByVideoId(req: express.Request, res: express.Response, next: express.NextFunction) {
    const { videoId } = req.query;

    if (!req.file || !videoId) {
        return res.status(400).json({ message: 'Missing cookie file or videoId' });
    }

    // 1. 쿠키 임시 파일로 저장
    const tempCookiePath = path.join(os.tmpdir(), `cookie-${uuidv4()}.txt`);
    fs.writeFileSync(tempCookiePath, req.file.buffer);

    const videoGetByYoutubeWatchId: any = await youtube_dl(`https://www.youtube.com/watch?v=${videoId}`, {
        dumpSingleJson: true,
        noCheckCertificates: true,
        noWarnings: true,
        skipDownload: true,
        cookies: tempCookiePath,
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
        message: 'youtube link generated.',
    });
}

export default {
    getVideoFeaturesByVideoId,
    getStreamVideoFeatruesVideo,
    uploadCookiesForStreamVideo,
};
