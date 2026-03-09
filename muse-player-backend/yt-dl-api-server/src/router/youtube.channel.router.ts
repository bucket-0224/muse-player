import express, { Router } from 'express';
import youtubeChannelController from '../controller/youtube.channel.controller';

const router = Router();

router.get('/channel-info', async (req, res, next) => {
    try {
        await youtubeChannelController.getYoutubeChannelInfoByChannelId(req, res, next);
    } catch (error) {
        next(error);
    }
});

router.get('/search', async (req, res, next) => {
    try {
        await youtubeChannelController.fetchYoutubeVideosByQuery(req, res, next);
    } catch (error) {
        next(error);
    }
});

router.post('/search-channel', async (req, res, next) => {
    try {
        await youtubeChannelController.getYoutubeChannelBySearchQuery(req, res, next);
    } catch (error) {
        next(error);
    }
});

router.get('/playlist-info', async (req, res, next) => {
    try {
        await youtubeChannelController.getPlaylistByPlaylistId(req, res, next);
    } catch (error) {
        next(error);
    }
});

router.get('/album-info', async (req, res, next) => {
    try {
        await youtubeChannelController.getYoutubeArtistAlbum(req, res, next);
    } catch (error) {
        next(error);
    }
});

export default router;
