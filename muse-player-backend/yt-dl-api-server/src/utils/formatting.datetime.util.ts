function formatDuration(seconds: number | undefined) {
    if (seconds === undefined) {
        return 'NaN';
    } else {
        const mins = Math.floor(seconds / 60);

        return `${mins} minute${mins !== 1 ? 's' : ''}`;
    }
}

export default formatDuration;
