import {Book} from "@/types/types";
import Image from "next/image";
import BookCard from "@/app/(storefront)/_components/BookCard/BookCard";

interface BookRowProps {
    // "Books" limit to 4 or less items
    books: Book[];
}

export default function BookRow({books}: BookRowProps) {
    return (
        <div className={'flex flex-row items-center justify-center gap-10'}>
            {books.slice(0, 4).map((book) => (
                <BookCard key={book.id} book={book} />
            ))}
        </div>
    )
}


