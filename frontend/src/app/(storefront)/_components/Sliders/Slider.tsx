'use client';
import {Book} from "@/types/types";
import {useEffect, useState} from "react";
import SliderControl from "@/app/(storefront)/_components/Sliders/SliderControl";
import SliderItem from "@/app/(storefront)/_components/Sliders/SliderItem";
import SliderDots from "@/app/(storefront)/_components/Sliders/SliderDots";
import {AnimatePresence, motion} from "framer-motion";
import {sliderVariants} from "@/motion/variants";

interface BestSellingSliderProps {
    books: Book[];
}

export default function Slider({ books }: BestSellingSliderProps) {
    const [current, setCurrent] = useState(0);
    const [direction, setDirection] = useState(0);
    const [isPaused, setIsPaused] = useState(false);
    const autoSwipeInterval = 5000;

    const prev = () =>
    {
        setCurrent((c) => (c - 1 + books.length) % books.length);
        setDirection(-1);
    }
    const next = () => {
        setCurrent((c) => (c + 1) % books.length);
        setDirection(1);
    }

    useEffect(() => {
        if (isPaused) return;
        const timer = setTimeout(() => {
            next();
        }, autoSwipeInterval);

        return () => clearTimeout(timer); // clear and restart every slide change
    }, [current, isPaused]);

    return (
        <div
            onMouseEnter={()  => setIsPaused(true)}
            onMouseLeave={() => setIsPaused(false)}
            className={'relative flex flex-col items-center w-full mx-auto'}>
            <div className={'flex w-full px-4 items-center justify-between gap-10 px-[calc(100%/10)]'}>
                <SliderControl direction="left" onClick={prev} />
                <AnimatePresence custom={direction} mode={"popLayout"}>
                    <motion.div
                        key={current}
                        custom={direction}
                        variants={sliderVariants}
                        initial="enter"
                        animate="center"
                        exit="exit"
                        className="w-full flex justify-center"
                    >
                        <SliderItem book={books[current]} />
                    </motion.div>
                </AnimatePresence>
                <SliderControl direction="right" onClick={next}/>
            </div>
            <SliderDots count={books.length} active={current} setActive={setCurrent}/>
        </div>
    )
}