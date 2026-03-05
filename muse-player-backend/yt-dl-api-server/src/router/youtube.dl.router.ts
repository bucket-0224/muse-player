import express, { Router } from 'express';
import youtubeVideoController from '../controller/youtube.dl.controller';
import multer from 'multer';
import path from 'path';
import fs from 'fs';

const router = Router();
const upload = multer({ storage: multer.memoryStorage() });

router.post('/features', upload.single('cookie'), async (req, res, next) => {
    try {
        await youtubeVideoController.getVideoFeaturesByVideoId(req, res, next);
    } catch (error) {
        next(error);
    }
});

router.use('/stream-audio', async (req, res, next) => {
    try {
        await youtubeVideoController.getStreamVideoFeatruesVideo(req, res);
    } catch (error) {
        next(error);
    }
});

router.post('/upload-cookie', upload.single('cookie'), async (req, res, next) => {
    try {
        await youtubeVideoController.uploadCookiesForStreamVideo(req, res, next);
    } catch (error) {
        next(error);
    }
});

export default router;
