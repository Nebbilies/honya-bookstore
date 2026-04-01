'use client';

import {Book} from "@/types/types";
import SectionTitle from "@/app/(storefront)/_components/SectionTitle";
import BookCard from "@/app/(storefront)/_components/BookCard/BookCard";
import SliderDots from "@/app/(storefront)/_components/Sliders/SliderDots";
import {useState} from "react";
import {AnimatePresence, motion, Variants} from "framer-motion";
import {sliderVariants} from "@/motion/variants";

interface BooksWithOfferProps {
    books: Book[];
}

interface RowProps {
    bookRow: Book[];
}

const Row = ({ bookRow }: RowProps) => {
    return (
        <div className={'flex gap-10 flex-wrap justify-center'}>
            <BookCard book={bookRow[0]} discount={50}></BookCard>
            <BookCard book={bookRow[1]} discount={30}></BookCard>
            <BookCard book={bookRow[2]} discount={30}></BookCard>
            <BookCard book={bookRow[3]} discount={30}></BookCard>
        </div>
    )
}


// placeholder only, no logic implementation
export default function BooksWithOffer({books}: BooksWithOfferProps) {
    const [currentRow, setCurrentRow] = useState(0);
    const [direction, setDirection] = useState(0);
    const rows: Book[][] = [[books[0], books[1], books[2], books[3]], [books[4], books[5], books[6], books[7]]];

    return (
        <section className={'flex flex-col items-center justify-center gap-[40px] py-12 bg-background-darker w-full overflow-hidden'}>
            <div className={'max-w-7xl w-full'}>
                <SectionTitle title={'Books With Offer'} helper={'GRAB YOUR OPPORTUNITY'}/>
            </div>
            <AnimatePresence custom={direction} mode={"wait"}>
                <motion.div className={'flex w-full gap-10 flex-wrap justify-center'}
                            key={currentRow}
                            custom={direction}
                            variants={sliderVariants}
                            initial={{opacity: 0}}
                            animate={{opacity: 1}}
                            exit={{opacity: 0}}
                >
                    <Row bookRow={rows[currentRow]}/>
                </motion.div>
            </AnimatePresence>
            <div className={'w-256 border-1 border-line-color'}></div>
            <SliderDots count={rows.length} active={currentRow} setActive={setCurrentRow}/>
        </section>
    )
}