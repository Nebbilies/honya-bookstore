import { setupWorker } from "msw/browser";
import { handlers } from "./handlers";

export const worker = setupWorker(...handlers);

worker.start().then(() => {
    console.log('Mock worker is running');
});