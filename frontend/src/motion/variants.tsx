import {Variants} from "framer-motion";

export const sliderVariants: Variants = {
    enter: (dir: number) => ({
        x: dir > 0 ? 150 : -150,
        opacity: 0,
    }),
    center: (dir: number) => ({
        x: 0,
        opacity: 1,
        transition: { duration: 0.5, ease: "easeOut", delay: 0.5 },
    }),
    exit: (dir: number) => ({
        x: dir > 0 ? -150 : 150,
        opacity: 0,
        transition: { duration: 0.4, ease: "easeIn" },
    }),
};