const log = (message: string) => ({
    warning: () => {
        console.log(`\x1b[43m ${new Date().toISOString()} - ${message} \x1b[0m`);
    },
    error: () => {
        console.log(`\x1b[41m ${new Date().toISOString()} - ${message} \x1b[0m`);
    },
    sucess: () => {
        console.log(`\x1b[42m ${new Date().toISOString()} - ${message} \x1b[0m`);
    },
});

export default log;
