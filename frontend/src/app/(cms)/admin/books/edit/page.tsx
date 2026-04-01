import BookForm from "@/app/(cms)/admin/books/_components/BookForm";
import {auth} from "@/auth";
import {getCategories} from "@/app/(cms)/admin/categories/page";
import {CategoryResponse} from "@/types/types";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: 'Edit Book',
    description: 'Edit the details of an existing book in the bookstore',
}

export default async function BookEditPage({ searchParams } : { searchParams: Promise<{ id: string }> }) {
    const session = await auth();
    const { id } = await searchParams;
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/books/${id}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            authorization: `Bearer ${session?.accessToken}`
        }
    });

    if (!res.ok) {
        const errorData = await res.json();
        return (
            <main className={'flex flex-col h-full gap-6'}>
                <h1 className={'font-prata text-3xl'}>Edit Category</h1>
                <section className={'flex items-start justify-between gap-12 h-full w-full'}>
                    <p className="text-red-500">Failed to load book data: {errorData.message || 'Unknown error'}</p>
                </section>
            </main>
        )
    }

    const categoryData: CategoryResponse = await getCategories();
    const categories = categoryData.data;

    const bookData = await res.json();

    return (
        <main className={'flex flex-col gap-6 h-full'}>
            <h1 className={'font-prata text-3xl'}>Edit Book</h1>
            <BookForm
                mode={'edit'}
                initialData={bookData}
                categories={categories}
            />
        </main>
    )
}