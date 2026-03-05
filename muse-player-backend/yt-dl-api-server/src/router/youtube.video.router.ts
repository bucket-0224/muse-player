import express, { Router } from 'express';
import youtubeVideoController from '../controller/youtube.video.controller';

const router = Router();

router.get('/search', async (req, res, next) => {
    try {
        await youtubeVideoController.getYoutubeMusicBySearchQuery(req, res, next);
    } catch (error) {
        next(error);
    }
});

export default router;
