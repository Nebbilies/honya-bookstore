'use client';
import {Book} from "@/types/types";
import {useMemo, useState} from "react";
import SliderControl from "@/app/(storefront)/_components/Sliders/SliderControl";
import {AnimatePresence, motion} from "framer-motion";
import {sliderVariants} from "@/motion/variants";
import Icon from "@/components/Icon";
import Button from "@/components/Button";
import {useCart} from "@/app/(storefront)/_context/CartContext";
import {getBookCover} from "@/lib/utils";
import Image from "next/image";


interface BookDetailSectionProps {
    book: Book;
}

export default function BookDetailSection({ book }: BookDetailSectionProps) {
    const cartContext = useCart();
    const [current, setCurrent] = useState(0);
    const [thumbPage, setThumbPage] = useState(0);
    const [direction, setDirection] = useState(0);

    const imageUrls = book.media.map((img) => img.url);
    const visibleThumbs = 4;
    const totalPages = useMemo(() => {
        return Math.ceil(imageUrls.length / visibleThumbs);
    }, [imageUrls.length]);

    const prev = () =>
    {
        const newIndex = (current - 1 + imageUrls.length) % imageUrls.length;
        setCurrent(newIndex);
        if (newIndex < thumbPage * visibleThumbs || newIndex >= (thumbPage + 1) * visibleThumbs) {
            setThumbPage(Math.floor(newIndex / visibleThumbs));
        }
        setDirection(-1);
    }
    const next = () => {
        const newIndex = (current + 1) % imageUrls.length;
        setCurrent(newIndex);
        if (newIndex < thumbPage * visibleThumbs || newIndex >= (thumbPage + 1) * visibleThumbs) {
            setThumbPage(Math.floor(newIndex / visibleThumbs));
        }
        setDirection(1);
    }
    const changeThumbPage = (dir: number) => {
        setThumbPage((p) => Math.max(0, Math.min(totalPages - 1, p + dir)))
    }

    const thumbsToShow = imageUrls.slice(
        thumbPage * visibleThumbs,
        thumbPage * visibleThumbs + visibleThumbs
    )

    return (
        <section className={'w-full bg-cover'} style={{backgroundImage: `url('/images/bgui/best-selling-bg.png')`}}>
            <div className={'w-4/5 flex items-center justify-center gap-[calc(100%/12)] mx-auto py-20'}>
                <div className={'flex-col items-center justify-center'}>
                    <div className={'p-[40px] bg-[#dedcd4] rounded-lg inset-shadow-sm shadow-inner gap-[75px] flex items-center'}>
                        <SliderControl direction={"left"} onClick={prev}/>
                        <AnimatePresence custom={direction} mode={"popLayout"}>
                            <motion.img
                                layout={"preserve-aspect"}
                                key={current}
                                src={imageUrls[current]}
                                alt={book.title}
                                width={400}
                                height={400}
                                loading={"eager"}
                                className={'shadow-xl h-[400px] w-[250px] object-contain aspect-auto rounded-md'}
                                custom={direction}
                                variants={sliderVariants}
                                initial="enter"
                                animate="center"
                                exit="exit"
                            />
                        </AnimatePresence>
                        <SliderControl direction={"right"} onClick={next}/>
                    </div>
                    <div className="flex items-center justify-center gap-[calc(100%/6)] mt-6 w-full">
                        <button
                            className="w-8 h-8 flex items-center justify-center bg-white rounded-full shadow-md disabled:opacity-30 cursor-pointer disabled:cursor-not-allowed"
                            onClick={() => changeThumbPage(-1)}
                            disabled={thumbPage === 0}
                        >
                            ←
                        </button>

                        <div className="flex gap-5 justify-center">
                            {thumbsToShow.map((url, index) => {
                                const actualIndex = thumbPage * visibleThumbs + index
                                const isActive = actualIndex === current
                                return (
                                    <Image
                                        key={actualIndex}
                                        src={url}
                                        alt=""
                                        height={80}
                                        width={50}
                                        onClick={() => setCurrent(actualIndex)}
                                        className={`h-[80px] w-[50px] rounded-md cursor-pointer object-contain shadow-sm transition-all ${
                                            isActive
                                                ? "ring-2 ring-blue-500 scale-105"
                                                : "hover:scale-105 opacity-80"
                                        }`}
                                    />
                                )
                            })}
                        </div>

                        <button
                            className={`w-8 h-8 flex items-center justify-center bg-white rounded-full shadow-md disabled:opacity-30 cursor-pointer disabled:cursor-not-allowed`}
                            onClick={() => changeThumbPage(1)}
                            disabled={thumbPage >= totalPages - 1}
                        >
                            →
                        </button>
                    </div>
                </div>
                <div className={'flex flex-col gap-[30px] items-start justify-start w-2/5'}>
                    <div>
                        <span className={'font-prata text-[40px]'}>{book.title}</span>
                        <svg width="31" height="7" viewBox="0 0 31 7" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path
                                d="M0.921631 1C0.921631 1 3.02385 6 5.02877 6C7.0337 6 7.13099 1 9.13592 1C11.1408 1 11.2381 6 13.2431 6C15.248 6 15.3453 1 17.3502 1C19.3551 1 19.4524 6 21.4573 6C23.4623 6 23.5596 1 25.5645 1C27.5694 1 29.6716 6 29.6716 6"
                                stroke="#74642F" strokeWidth="2"/>
                        </svg>
                    </div>
                    <div className={'rating flex items-end gap-2'}>
                        {Array.from({length: 5}).map((_, i) => (
                            <Icon name={'star-empty'} size={50} key={i} className={'opacity-80'}/>
                        ))}
                        <span className={'font-medium text-lg'}>4.20</span>
                    </div>
                    <span className={'font-prata text-[25px]'}>{book.author}</span>
                    <span className={'font-plus-jakarta-sans tracking-wider text-[16px] leading-7 text-justify text-[#7a7a7a] line-clamp-2'}>
                        {book.description}
                    </span>
                    <span className={'font-prata text-price-color text-[30px] tracking-wide'}>{book.price.toLocaleString("vi-VN", {
                        style: "currency",
                        currency: "VND",
                    })}</span>
                    <Button onClick={() => cartContext.addToCart(book)} variant={"solid"} shape={'rect'} width={280} height={60} icon={'cart'} iconSize={30} iconPosition={'left'}
                            className={'font-plus-jakarta-sans text-[20px] rounded-[20px]'}>
                        Add to Cart
                    </Button>
                </div>
            </div>
        </section>
    )
}