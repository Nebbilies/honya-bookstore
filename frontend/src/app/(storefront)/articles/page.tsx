import { Metadata } from "next";
import { ArticleResponse } from "@/types/types";
import Breadcrumb, { BreadcrumbItemType } from "@/app/(storefront)/_components/Breadcrumb/Breadcrumb";
import { CustomPagination } from "@/components/Pagination/CustomPagination";
import ArticleCard from "@/app/(storefront)/_components/Article/ArticleCard";
import ArticleHero from "@/app/(storefront)/_components/Article/ArticleHero";

export const dynamic = 'force-dynamic';

export const metadata: Metadata = {
    title: 'Articles',
    description: 'Stories, guides and inspiration for readers — from the Honya Bookstore blog.',
};

async function getPublishedArticles(page: number): Promise<ArticleResponse> {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    const res = await fetch(`${apiBaseUrl}/articles/public?page=${page}&limit=9`, { cache: 'no-store' });
    if (!res.ok) {
        return {
            data: [],
            meta: { totalItems: 0, pageItems: 0, itemsPerPage: 9, totalPages: 0, currentPage: 1 },
        };
    }
    return res.json();
}

export default async function ArticleBlogPage({ searchParams }: { searchParams: Promise<{ page?: string }> }) {
    const params = await searchParams;
    const page = Number(params?.page) || 1;
    const data = await getPublishedArticles(page);
    const articles = data.data;

    const hero = page === 1 ? articles[0] : undefined;
    const gridArticles = page === 1 ? articles.slice(1) : articles;

    const breadcrumbItems: BreadcrumbItemType[] = [
        { label: 'Articles', href: '/articles' },
    ];

    return (
        <main className={'flex flex-col w-full max-w-[1000px] mx-auto px-5 pt-4 pb-20 gap-8'}>
            <Breadcrumb items={breadcrumbItems}/>

            {hero && <ArticleHero article={hero}/>}

            {gridArticles.length > 0 ? (
                <section className={'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-x-[30px] gap-y-10'}>
                    {gridArticles.map((article) => (
                        <ArticleCard key={article.id} article={article}/>
                    ))}
                </section>
            ) : !hero ? (
                <div className={'py-20 text-center font-prata text-2xl'}>No articles found</div>
            ) : null}

            {data.meta.totalPages > 1 && (
                <div className={'flex justify-center mt-4'}>
                    <CustomPagination totalPages={data.meta.totalPages}/>
                </div>
            )}
        </main>
    );
}
