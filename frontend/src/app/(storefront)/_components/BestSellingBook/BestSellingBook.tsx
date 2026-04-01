import {Book} from "@/types/types";
import Image from "next/image";
import Button from "@/components/Button";
import Link from "next/link";
import {getBookCover} from "@/lib/utils";

interface BestSellingBookProps {
    book: Book
}

export default function BestSellingBook({book} : BestSellingBookProps){
    return (
        <section className={'w-full flex items-center justify-center py-20 min-h-screen gap-20 bg-cover'} style={{backgroundImage: `url('/images/bgui/best-selling-bg.png')`}}>
            <Image src={getBookCover(book.media)} alt={book.title} width={400} height={500} loading={"lazy"} className={'shadow-2xl overflow-hidden h-[575px] w-auto'} />
            <div className={'max-w-[550px] flex flex-col gap-16'}>
                <div className={'font-prata text-[62px] gap-2 flex flex-col'}>
                    <span>Best Selling Book</span>
                    <svg width="31" height="7" viewBox="0 0 31 7" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path
                            d="M0.921631 1C0.921631 1 3.02385 6 5.02877 6C7.0337 6 7.13099 1 9.13592 1C11.1408 1 11.2381 6 13.2431 6C15.248 6 15.3453 1 17.3502 1C19.3551 1 19.4524 6 21.4573 6C23.4623 6 23.5596 1 25.5645 1C27.5694 1 29.6716 6 29.6716 6"
                            stroke="#74642F" strokeWidth="2"/>
                    </svg>
                </div>
                <div className={'font-plus-jakarta-sans text-[12px] font-bold uppercase tracking-[1px] opacity-50'}>
                    by {book.author}
                </div>
                <div className={'w-full flex flex-col text-left gap-2'}>
                    <span className={'font-prata text-[30px]'}>{book.title}</span>
                    <span className={'font-plus-jakarta-sans text-[16px] text-[#7a7a7a] line-clamp-3 overflow-hidden'}>{book.description}</span>
                    <span className={'font-prata text-[30px] text-price-color mt-4'}>{book.price.toLocaleString("vi-VN", {
                        style: "currency",
                        currency: "VND",
                    })}</span>
                </div>
                <Link href={`/books/${book.id}`}>
                    <Button
                        variant="outline"
                        shape="rect"
                        iconPosition="right"
                        icon="right-arrow"
                        className="font-plus-jakarta-sans border-2 text-md w-fit py-4 px-2 rounded-none tracking-wider"
                    >
                        READ MORE
                    </Button>
                </Link>
            </div>
        </section>
    )
}