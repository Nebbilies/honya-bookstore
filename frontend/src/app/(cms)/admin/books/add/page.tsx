import {auth} from "@/auth";
import {CategoryResponse} from "@/types/types";
import {Metadata} from "next";
import BookForm from "@/app/(cms)/admin/books/_components/BookForm";

export const metadata: Metadata = {
    title: 'Add Book',
    description: 'Add a new book to the bookstore',
}

export default async function AddBookPage() {
    const session = await auth;
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/categories`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    });
    if (!res.ok) {
        throw new Error('Failed to fetch categories');
    }

    const categoriesData: CategoryResponse = await res.json();
    const categories = categoriesData.data;

    return (
        <main className={'flex flex-col gap-6'}>
            <h1 className={'font-prata text-3xl'}>Add Book</h1>
            <BookForm categories={categories} mode={'add'}/>
        </main>
    )
}