import {
    getArtist,
    listMusicFromAlbum,
    searchForArtists,
    getPlaylist,
    listMusicFromPlaylist,
} from 'youtube-music-ts-lib';
import express, { NextFunction } from 'express';
import formatDuration from '../utils/formatting.datetime.util';
import YouTube from 'youtube-sr';

// 개별 아이템에 type이 포함된 통합 규격
interface UnifiedSearchItem {
    id: string;
    type: 'VIDEO' | 'ARTIST'; // 섞여있으므로 개별 아이템이 자신의 타입을 알아야 함
    title: string;
    artist: string | undefined;
    subscribers: string | undefined;
    thumbnailUrl: string;
}

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

async function fetchYoutubeVideosByQuery(req: express.Request, res: express.Response, next: express.NextFunction) {
    try {
        const { searchQuery } = req.query;

        if (!searchQuery) {
            return res.status(400).json({ message: 'searchQuery is required' });
        }

        const query = searchQuery as string;

        // 두 개의 API를 병렬로 동시 호출하여 시간 단축
        const [videos, artists] = await Promise.all([
            YouTube.search(query, { type: 'video', limit: 20 }),
            searchForArtists(query, { lang: 'ko' }).catch(() => []), // 에러 발생 시 빈 배열 반환으로 전체 뻗음 방지
        ]);

        // 1. 비디오 데이터 매핑
        const mappedVideos: UnifiedSearchItem[] = videos.map((video: any) => {
            console.log(video.durationFormatted);

            return {
                id: video.id,
                type: 'VIDEO',
                title: video.title,
                artist: video.channel.name,
                subscribers: undefined,
                durationFormatted: video.durationFormatted,
                thumbnailUrl: video.thumbnail?.url || video.thumbnail || '',
            };
        });

        // 2. 아티스트 데이터 매핑
        const mappedArtists: UnifiedSearchItem[] = artists.map((artist: any) => ({
            id: artist.artistId,
            type: 'ARTIST',
            title: artist.name,
            subscribers: artist.subscribers,
            artist: undefined,
            thumbnailUrl: artist.thumbnailUrl || '',
        }));

        const combinedItems = [...mappedArtists, ...mappedVideos];

        return res.status(200).json({
            message: 'Successfully retrieved shuffled search results',
            items: combinedItems,
        });
    } catch (error) {
        next(error);
    }
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
    fetchYoutubeVideosByQuery,
};
