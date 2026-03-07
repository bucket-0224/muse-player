import express, { Router } from 'express';
import youtubeVideoController from '../controller/youtube.dl.controller';
import multer from 'multer';
import path from 'path';
import fs from 'fs';

const router = Router();
const upload = multer({ storage: multer.memoryStorage() });

router.get('/features', async (req, res, next) => {
    try {
        await youtubeVideoController.getVideoFeaturesByVideoId(req, res, next);
    } catch (error) {
        next(error);
    }
});

router.use('/feature-shorts', async (req, res, next) => {
    try {
        await youtubeVideoController.getShortsVideo(req, res);
    } catch (error) {
        next(error);
    }
});

router.use('/stream-shorts', async (req, res, next) => {
    try {
        await youtubeVideoController.getStreamShortFeaturesVideo(req, res);
    } catch (error) {
        next(error);
    }
});

router.use('/stream-features', async (req, res, next) => {
    try {
        await youtubeVideoController.getStreamVideoFeaturesVideo(req, res);
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
