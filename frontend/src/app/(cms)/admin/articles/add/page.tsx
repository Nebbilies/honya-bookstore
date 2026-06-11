import { Metadata } from "next";
import ArticleForm from "@/app/(cms)/admin/articles/_components/ArticleForm";

export const metadata: Metadata = {
    title: 'Add Article',
    description: 'Add a new article to the bookstore',
}

export default function AddArticlePage() {
    return (
        <main className={'flex flex-col gap-6'}>
            <h1 className={'font-prata text-3xl'}>Add Article</h1>
            <ArticleForm mode={'add'}/>
        </main>
    );
}
