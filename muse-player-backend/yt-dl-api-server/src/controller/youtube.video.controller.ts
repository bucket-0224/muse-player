import { searchForMusic } from 'youtube-music-ts-lib';
import search, * as youtubeSearch from 'youtube-search';
import express, { NextFunction } from 'express';
import YouTube from 'youtube-sr';

var opts: youtubeSearch.YouTubeSearchOptions = {
    maxResults: 20,
    key: 'AIzaSyAbrkQmWFrRfpyAmXJam8Ac45RuTyGwOU4',
};

async function getYoutubeMusicBySearchQuery(req: express.Request, res: express.Response, next: express.NextFunction) {
    const { searchQuery } = req.query;

    const videos = await YouTube.search(searchQuery as string, {
        type: 'video',
        limit: 20,
    });

    return res.status(200).json({
        message: 'get music lists',
        videos: videos,
    });
}

export default {
    getYoutubeMusicBySearchQuery,
};
