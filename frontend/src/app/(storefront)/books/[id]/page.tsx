import Breadcrumb, {BreadcrumbItemType} from "@/app/(storefront)/_components/Breadcrumb/Breadcrumb";
import BookDetailSection from "@/app/(storefront)/books/_components/BookDetailSection";
import {Book} from "@/types/types";
import BookDescriptionSection from "@/app/(storefront)/books/_components/BookDescriptionSection";
import BookReviewSection from "@/app/(storefront)/books/_components/BookReviewSection";
import { Metadata } from "next";

export const dynamic = 'force-dynamic';

export async function generateMetadata({ params }: { params : { id: number } }): Promise<Metadata> {
    const { id } = await params;
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/books/${id}`, { cache: 'no-store' });
    if (!res.ok) {
        return {
            title: 'Book Not Found',
        }
    };
    const book: Book = await res.json();
    return {
        title: book.title,
        description: book.description?.slice(0, 160) || 'Discover your next favorite read at Honya Bookstore.',
    }
}

export default async function BookDetailPage({ params }: { params : { id: number } }) {
    const { id } = await params;
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/books/${id}`, { cache: 'no-store' });
    if (!res.ok) {
        throw new Error('Failed to fetch book details');
    }
    const book: Book = await res.json();

    const breadcrumbItems: BreadcrumbItemType[] = [
        { label: 'Books', href: '/books' },
        { label: book.title, href: `/books/${book.id}` },
    ]

    return (
        <main className={'py-10 w-full flex flex-col gap-10 items-center'}>
            <div className={'mx-auto w-4/5'}>
                <Breadcrumb items={breadcrumbItems} />
            </div>
            <BookDetailSection book={book}/>
            <BookDescriptionSection description={book.description}/>
            <BookReviewSection bookId={book.id}/>
        </main>
    );
}