import {Book} from "@/types/types";
import Button from "@/components/Button";
import Image from "next/image";
import {getBookCover} from "@/lib/utils";

interface SliderItemProps {
    book: Book;
}

export default function SliderItem({ book }: SliderItemProps) {
    return (
        <div className={'flex flex-col md:flex-row items-center justify-between gap-20'}>
            <div className={'max-w-xl text-center md:text-left gap-10 flex flex-col'}>
                <h2 className={'font-prata text-5xl line-clamp-2 py-2'}>
                    {book.title}
                </h2>
                <span className={'font-normal text-md font-plus-jakarta-sans opacity-50 line-clamp-3 tracking-wide overflow-hidden'}>
                    {book.description}
                </span>
                <Button
                    variant={'outline'}
                    shape={'rect'}
                    iconPosition={'right'}
                    icon={'right-arrow'}
                    onClick={() => {
                        window.location.href = `/books/${book.id}`;
                    }}
                    className={'font-plus-jakarta-sans opacity-70 text-md w-fit py-4 px-2 rounded-none tracking-wider'}
                    >
                    READ MORE
                </Button>
            </div>
            <Image src={getBookCover(book.media)} alt={book.title} width={250} height={400} loading={'lazy'} className={'shadow-xl h-[400px] w-[250px] object-cover'} />
            <svg width="340" height="389" viewBox="0 0 340 389" fill="none" xmlns="http://www.w3.org/2000/svg" className={'absolute right-115 -z-10'}>
                <path
                    d="M303.988 356.075C330 327.231 346.036 301.582 336.815 265.15C327.1 226.768 278.338 229.692 257.175 195.38C222.459 139.093 305.765 73.5226 253.633 29.9121C223.58 4.77155 194.984 -1.15366 154.322 0.839692C96.3897 3.67973 62.1394 31.373 30.4028 75.8098C-18.6451 144.485 -3.71198 218.8 48.948 285.198C110.084 362.284 237.738 429.538 303.988 356.075Z"
                    stroke="#E5E4DE"/>
            </svg>

        </div>
    )
}