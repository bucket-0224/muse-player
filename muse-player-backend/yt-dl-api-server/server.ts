import express from 'express';
import youtubeDlRouter from './src/router/youtube.dl.router';
import youtubeChannelRouter from './src/router/youtube.channel.router';
import youtubeVideoRouter from './src/router/youtube.video.router';
import log from './src/utils/log.util';
import path from 'path';
import { spawn } from 'child_process';

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use('/video', youtubeDlRouter);
app.use('/channel', youtubeChannelRouter);
app.use('/videos', youtubeVideoRouter);

app.use('/streams', express.static(path.join(process.cwd(), 'streams')));

const port = 5858;

spawn('ffmpeg', ['-version']);

app.listen(port, () => {
    log(`Server running on port ${port}`).sucess();
});
