import ffmpeg from 'fluent-ffmpeg';

async function getAudioDuration(audioUrl: string): Promise<number> {
    return new Promise((resolve, reject) => {
        ffmpeg.ffprobe(audioUrl, (err, metadata) => {
            if (err) return reject(err);
            resolve(metadata.format.duration ?? 0); // 초 단위
        });
    });
}

export default getAudioDuration;
