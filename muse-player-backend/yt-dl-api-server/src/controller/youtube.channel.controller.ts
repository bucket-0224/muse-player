import {
    getArtist,
    listMusicFromAlbum,
    searchForArtists,
    getPlaylist,
    listMusicFromPlaylist,
} from 'youtube-music-ts-lib';
import express, { NextFunction } from 'express';
import formatDuration from '../utils/formatting.datetime.util';

async function getYoutubeChannelInfoByChannelId(
    req: express.Request,
    res: express.Response,
    next: express.NextFunction
) {
    const { channelId } = req.query;
    const artistAlbums = await getArtist(channelId as string);

    return res.status(200).json({
        message: 'get channel info success',
        artist: artistAlbums,
    });
}

async function getYoutubeChannelBySearchQuery(req: express.Request, res: express.Response, next: express.NextFunction) {
    const { searchQuery } = req.body;

    const searchedArtists = await searchForArtists(searchQuery as string, {
        lang: 'ko',
    });

    return res.status(200).json({
        message: 'get channel info lists',
        artist: searchedArtists,
    });
}

async function getPlaylistByPlaylistId(req: express.Request, res: express.Response, next: express.NextFunction) {
    const { playlistId } = req.query;

    const playListDetail = await listMusicFromPlaylist(playlistId as string, 'a372aadfb42b2dd75ccabeb97ffbc00a');

    return res.status(200).json({
        message: 'get playlist info',
        playList: playListDetail,
    });
}

async function getYoutubeArtistAlbum(req: express.Request, res: express.Response, next: express.NextFunction) {
    const { channelId, albumId } = req.query;

    // const album = await authenticationoutubeMusic.getAlbum(albumId as string);

    const artistDetail = await getArtist(channelId as string, { lang: 'ko' });
    const albumDetail = await listMusicFromAlbum(albumId as string, 'a372aadfb42b2dd75ccabeb97ffbc00a');

    return res.status(200).json({
        message: 'get musics success',
        albumName:
            artistDetail.albums?.find((item) => item?.albumId == albumId)?.title ??
            artistDetail.singles?.find((item) => item?.albumId == albumId)?.title ??
            '',
        albumThumbnail: albumDetail.albumThumbnail,
        artist: {
            name: artistDetail.name ?? '',
            thumbnails: artistDetail?.thumbnails ?? [],
            subscribers: artistDetail?.subscribers ?? '',
        },
        totalMusicCount: albumDetail.albumItems.length,
        totalPlayTime: formatDuration(
            albumDetail.albumItems.reduce((sum, item) => (sum ?? 0) + (item.duration?.totalSeconds || 0), 0)
        ),
        musics: albumDetail.albumItems,
    });
}

export default {
    getYoutubeChannelInfoByChannelId,
    getYoutubeChannelBySearchQuery,
    getYoutubeArtistAlbum,
    getPlaylistByPlaylistId,
};
