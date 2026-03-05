import fs from 'fs';

function waitForFile(filePath: string, timeoutMs = 1200): Promise<void> {
    return new Promise((resolve, reject) => {
        const start = Date.now();
        const timer = setInterval(() => {
            if (fs.existsSync(filePath)) {
                clearInterval(timer);
                resolve();
            }
            if (Date.now() - start > timeoutMs) {
                clearInterval(timer);
                reject(new Error('HLS start timeout'));
            }
        }, 40); // 25fps 감지
    });
}

export default waitForFile;
