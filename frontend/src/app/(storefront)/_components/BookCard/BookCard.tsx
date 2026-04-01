'use client';
import {Book} from "@/types/types";
import Image from "next/image";
import {useState} from "react";
import {AnimatePresence, motion} from "framer-motion";
import Link from "next/link";
import {getBookCover} from "@/lib/utils";
import {useCart} from "@/app/(storefront)/_context/CartContext";

interface BookCardProps {
    discount?: number;
    showCartButton?: boolean;
    book: Book;
}

export default function BookCard({book, discount = 0, showCartButton = false}: BookCardProps) {
    const { addToCart } = useCart();
    const [isHovered, setIsHovered] = useState(false);
    return (
        <div className={'flex flex-col items-center justify-center bg-transparent w-fit gap-4 relative'}>
            {discount !== 0 && (
                <div className={'discount-badge absolute bg-price-color text-white font-plus-jakarta-sans text-[12px] px-2 py-1 z-1 top-0 left-0'}>
                    {discount}% Off
                </div>
            )}
            <div className={'book-cover flex p-8 bg-[#efeee8] rounded-lg relative transition-all duration-200'}
                    onMouseEnter={() => setIsHovered(true)}
                    onMouseLeave={() => setIsHovered(false)}>
                <Image src={getBookCover(book.media)} alt={"cover"} width={156} height={250} className={'overflow-hidden h-[250px] w-[156px] shadow-xl object-cover'} />
                {showCartButton && isHovered && (
                    <>
                        <motion.div className={'absolute w-full h-full left-0 bottom-0 bg-white opacity-10'}
                                    initial={{opacity: 0}}
                                    animate={{opacity: 0.6}}
                                    exit={{opacity: 0}}
                                    transition={{duration: 0.2}}>
                        </motion.div>
                        <motion.button className={'absolute bottom-[calc(var(--spacing)*8)] left-0 w-full ' +
                            'bg-black text-white font-plus-jakarta-sans text-[20px] font-bold uppercase tracking-widest px-2 py-3 cursor-pointer'}
                                       initial={{opacity: 0}}
                                       animate={{opacity: 1}}
                                       exit={{opacity: 0}}
                                       transition={{duration: 0.2}}
                                       onClick={() => addToCart(book)}
                        >
                            ADD TO CART
                        </motion.button>
                    </>
                )}
            </div>
            <div className={'book-info flex flex-col items-center w-full'}>
                <Link href={`/books/${book.id}`} className={'font-prata text-[16px] text-center hover:text-blue-600 transition-all duration-200'}>
                    {book.title}
                </Link>
                <span className={'font-plus-jakarta-sans text-[16px] text-center opacity-50'}>
                    {book.author}
                </span>
                <span className={'font-prata text-[21px] text-center mt-2 text-price-color tracking-wide'}>
                    {discount === 0 ? book.price.toLocaleString("vi-VN", {
                    style: "currency",
                    currency: "VND",
                }) : (book.price * (1 - discount / 100)).toLocaleString("vi-VN", {
                        style: "currency",
                        currency: "VND",
                    })}
                </span>
            </div>
        </div>
    )
}