import CategoryForm from "@/app/(cms)/admin/categories/_components/CategoryForm";
import {Category} from "@/types/types";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: 'Edit Category',
    description: 'Edit an existing category in the bookstore',
}

export default async function CategoryEditPage({ searchParams }: { searchParams: Promise<{ slug: string }> }) {
    const { slug } = await searchParams;
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/categories/${slug}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
        cache: 'no-store'
    });

    if (!res.ok) {
        const errorData = await res.json();
        console.error('Failed to fetch category:', errorData.message || 'Unknown error');
        return (
            <main className={'flex flex-col h-full gap-6'}>
                <h1 className={'font-prata text-3xl'}>Edit Category</h1>
                <section className={'flex items-start justify-between gap-12 h-full w-full'}>
                    <p className="text-red-500">Failed to load category data: {errorData.message || 'Unknown error'}</p>
                </section>
            </main>
        )
    }

    const categoryData: Category = await res.json();

    return (
        <main className={'flex flex-col h-full gap-6'}>
            <h1 className={'font-prata text-3xl'}>Edit Category</h1>
            <section className={'flex items-start justify-between gap-12 h-full w-1/2'}>
                <CategoryForm
                    mode="edit"
                    initialData={categoryData}
                    categoryId={categoryData.id}
                />
            </section>
        </main>
    )
}