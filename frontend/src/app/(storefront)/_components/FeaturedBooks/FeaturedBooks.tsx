import { Book } from "@/types/types";
import BookRow from "../BookRow/BookRow";
import SectionTitle from "@/app/(storefront)/_components/SectionTitle";

interface BookRowProps {
    books: Book[];
}

export default function FeaturedBooks({ books }: BookRowProps) {
    // random by date
    const today: string = new Date().toISOString().split("T")[0];
    let seed: number = 0;
    for (let i = 0; i < today.length; i++) {
        seed += today.charCodeAt(i);
    }

    function seededRandom(seed: number) {
        const x = Math.sin(seed++) * 10000;
        return x - Math.floor(x);
    }

    const used = new Set<number>();
    const indices: number[] = [];
    while (indices.length < 4 && indices.length < books.length) {
        const idx = Math.floor(seededRandom(seed + indices.length) * books.length);
        if (!used.has(idx)) {
            used.add(idx);
            indices.push(idx);
        }
    }

    const featuredBooks = indices.map(i => books[i]);

    return (
        <section className={'flex flex-col items-center justify-center py-12 gap-[40px]'}>
            <SectionTitle title={'Featured Books'} helper={'SOME QUALITY ITEMS'}/>
            <BookRow books = {featuredBooks}/>
        </section>
    )
}