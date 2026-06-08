import { Metadata } from "next";
import { auth } from "@/auth";
import { Article } from "@/types/types";
import ArticleForm from "@/app/(cms)/admin/articles/_components/ArticleForm";

export const metadata: Metadata = {
    title: 'Edit Article',
    description: 'Edit the details of an existing article in the bookstore',
}

export default async function ArticleEditPage({ searchParams }: { searchParams: Promise<{ id: string }> }) {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    const session = await auth();
    const { id } = await searchParams;

    const res = await fetch(`${apiBaseUrl}/articles/${id}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            authorization: `Bearer ${session?.accessToken}`,
        },
        cache: 'no-store',
    });

    if (!res.ok) {
        const errorData = await res.json().catch(() => ({}));
        return (
            <main className={'flex flex-col h-full gap-6'}>
                <h1 className={'font-prata text-3xl'}>Edit Article</h1>
                <p className="text-red-500">Failed to load article data: {errorData.message || 'Unknown error'}</p>
            </main>
        );
    }

    const articleData: Article = await res.json();

    return (
        <main className={'flex flex-col gap-6 h-full'}>
            <h1 className={'font-prata text-3xl'}>Edit Article</h1>
            <ArticleForm mode={'edit'} initialData={articleData}/>
        </main>
    );
}
