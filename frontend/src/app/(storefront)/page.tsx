import {Book, BookResponse} from "@/types/types";
import Slider from "@/app/(storefront)/_components/Sliders/Slider";
import {Suspense} from "react";
import Icon from "@/components/Icon";
import SectionTitle from "@/app/(storefront)/_components/SectionTitle";
import FeaturedBooks from "@/app/(storefront)/_components/FeaturedBooks/FeaturedBooks";
import BestSellingBook from "@/app/(storefront)/_components/BestSellingBook/BestSellingBook";
import PopularBooks from "@/app/(storefront)/_components/PopularBooks/PopularBooks";
import Image from "next/image";
import BooksWithOffer from "@/app/(storefront)/_components/BooksWithOffer/BooksWithOffer";
import { Metadata } from "next";

export const dynamic = 'force-dynamic';

export const metadata: Metadata = {
    title: 'Home',
    description: 'Welcome to Honya Bookstore - Your one-stop shop for the latest and greatest books across all genres. Discover your next favorite read with us!',
}

export default async function landingPage() {
    // fetch books
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/books`, {cache: 'no-store'});
    const data: BookResponse = await res.json();
    const books = data.data;
    const bestSellingBooks = books.sort((a, b) => b.purchaseCount - a.purchaseCount).slice(0, 4);
    return (
        <main className={'flex flex-col w-full items-center'}>
            <Suspense fallback={<div>Loading...</div>}>
                <section className={'py-32 w-full'}>
                    <Slider books={bestSellingBooks}/>
                </section>
            </Suspense>
            <section className={'flex flex-col items-center justify-center mt-16 py-12 bg-background-darker w-full gap-10'}>
                    <div className={'w-3/5'}>
                        <SectionTitle title={'Why shop with us?'}/>
                    </div>
                    <div
                        className={'flex justify-center w-full gap-40 font-plus-jakarta-sans uppercase font-bold tracking-wide text-[#94928b] text-[20px]'}>
                        <div className={'flex flex-col items-center justify-center gap-4'}>
                            <Icon name={"timer"} size={50} color={'#94928b'}></Icon>
                            <span>Latest Books</span>
                        </div>
                        <div className={'flex flex-col items-center justify-center gap-4'}>
                            <Icon name={"delivery"} size={50} color={'#94928b'}></Icon>
                            <span>FAST DELIVERY</span>
                        </div>
                        <div className={'flex flex-col items-center justify-center gap-4'}>
                            <Icon name={"handshake"} size={50} color={'#94928b'}></Icon>
                            <span>TRUSTED SITES</span>
                        </div>
                    </div>
            </section>
            <FeaturedBooks books={books}/>
            <BestSellingBook book={books[0]}/>
            <PopularBooks books={books}/>
            <div className={'w-256 border-1 border-line-color'}></div>
            <section className={'flex flex-col items-center justify-center py-8 gap-4 py-4'}>
                <h3 className={'font-prata text-[40px]'}>Quote Of The Day</h3>
                <Image src={'/images/bgui/pattern2.svg'} alt={'pattern'} width={30} height={10} className={'w-[40px] h-auto overflow-hidden'}/>
                <span className={'font-plus-jakarta-sans text-center text-[20px] text-[#7a7a7a] max-w-2xl'}>
                    “The more that you read, the more things you will know. The more that you learn, the more places you’ll go.”
                </span>
                <span className={'font-prata text-[25px]'}>
                    Dr. Seuss
                </span>
            </section>
            <BooksWithOffer books={books}/>
            <section className={'flex w-fit  items-center justify-between py-20 gap-4 px-4'}>
                <div className={'flex flex-col items-start gap-2 max-w-xl'}>
                    <span className={'text-[44px] font-prata'}>Subscribe To Our Newsletter</span>
                    <svg width="31" height="7" viewBox="0 0 31 7" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path
                            d="M0.921631 1C0.921631 1 3.02385 6 5.02877 6C7.0337 6 7.13099 1 9.13592 1C11.1408 1 11.2381 6 13.2431 6C15.248 6 15.3453 1 17.3502 1C19.3551 1 19.4524 6 21.4573 6C23.4623 6 23.5596 1 25.5645 1C27.5694 1 29.6716 6 29.6716 6"
                            stroke="#74642F" strokeWidth="2"/>
                    </svg>
                </div>
                <div className={'flex flex-col gap-16'}>
                    <span className={'text-[16px] font-plus-jakarta-sans text-[#7a7a7a] max-w-lg'}>
                        Sit with comfort, free and full of energy, this pain is felt while being connected. Elite and focused, full of energy.
                    </span>
                    <input type="email" placeholder="Enter your email" className={'border-1 border-x-0 border-t-0 border-line-color focus:border-gray-700 px-4 py-3 w-72 bg-transparent font-plus-jakarta-sans text-[16px] placeholder:text-[#7a7a7a] outline-none'}/>
                </div>
            </section>
        </main>
    );
}