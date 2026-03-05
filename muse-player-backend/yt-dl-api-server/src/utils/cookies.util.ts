import fs from 'fs';
import path from 'path';

export function loadCookiesHeader(): string {
    const cookiePath = path.resolve(__dirname, '../controller/cookies/cookies.txt');

    console.log('🍪 cookiePath =', cookiePath);

    const lines = fs.readFileSync(cookiePath, 'utf8').split('\n');

    return lines
        .filter((l) => l && !l.startsWith('#'))
        .map((l) => {
            const parts = l.split('\t');
            if (parts.length < 7) return '';
            return `${parts[5]}=${parts[6]}`;
        })
        .filter(Boolean)
        .join('; ');
}
