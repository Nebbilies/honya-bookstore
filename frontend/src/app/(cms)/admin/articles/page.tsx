import Image from "next/image";
import { Metadata } from "next";
import { auth } from "@/auth";
import { ArticleResponse } from "@/types/types";
import { CustomPagination } from "@/components/Pagination/CustomPagination";
import ArticleHeaderOptions from "@/app/(cms)/admin/articles/_components/ArticleHeaderOptions";
import ArticleSortableHeader from "@/app/(cms)/admin/articles/_components/ArticleSortableHeader";
import ArticleTableActions from "@/app/(cms)/admin/articles/_components/ArticleTableActions";

export const metadata: Metadata = {
    title: 'Article List',
    description: 'Manage the list of articles in the bookstore',
}

async function getArticles(search: string, page: number, sort?: string, order?: string): Promise<ArticleResponse> {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    const session = await auth();
    const params = new URLSearchParams();
    if (search) params.set('search', search);
    if (sort) params.set('sort', sort);
    if (order) params.set('order', order);
    params.set('page', page.toString());
    params.set('limit', '10');

    const res = await fetch(`${apiBaseUrl}/articles?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            authorization: `Bearer ${session?.accessToken}`,
        },
        cache: 'no-store',
    });

    if (!res.ok) {
        const errorData = await res.json().catch(() => ({}));
        console.error('Failed to fetch articles:', errorData.message || 'Unknown error');
        return {
            data: [],
            meta: { totalItems: 0, pageItems: 0, itemsPerPage: 10, totalPages: 0, currentPage: 1 },
        };
    }

    return res.json();
}

export default async function ArticleListPage({ searchParams }: { searchParams: Promise<{ search?: string; page?: string; sort?: string; order?: string }> }) {
    const params = await searchParams;
    const search = params?.search || '';
    const page = Number(params?.page) || 1;
    const articlesData = await getArticles(search, page, params?.sort, params?.order);
    const articles = articlesData.data;

    return (
        <main className={'flex flex-col gap-6'}>
            <div className={'flex justify-between items-center'}>
                <h1 className={'font-prata text-3xl'}>Article List</h1>
                <ArticleHeaderOptions/>
            </div>
            <table className="w-full border-collapse rounded-lg overflow-hidden shadow-sm">
                <thead>
                <tr className="bg-gray-100 text-left text-[16px] font-bold text-gray-700">
                    <th className="px-4 py-3 border-r border-gray-200">Thumbnail</th>
                    <ArticleSortableHeader column="title" label="Title"/>
                    <th className="px-4 py-3 border-r border-gray-200">Slug</th>
                    <th className="px-4 py-3 border-r border-gray-200">Tags</th>
                    <th className="px-4 py-3 border-r border-gray-200">Status</th>
                    <ArticleSortableHeader column="createdAt" label="Date Created"/>
                    <th className="px-4 py-3 text-center">Actions</th>
                </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 text-sm text-gray-700">
                {articles.length === 0 ? (
                    <tr>
                        <td colSpan={7} className="text-center py-4">No articles found</td>
                    </tr>
                ) : (
                    articles.map((article) => (
                        <tr key={article.id} className="hover:bg-gray-50 transition-colors duration-200">
                            <td className="px-4 py-3 border-gray-100">
                                <Image
                                    src={article.thumbnailUrl || '/images/fallbackBookImage.png'}
                                    alt={article.title}
                                    width={80}
                                    height={60}
                                    className="h-[60px] w-[80px] object-cover rounded-md shadow-sm"
                                />
                            </td>
                            <td className="px-4 py-3 border-gray-100 font-medium">{article.title}</td>
                            <td className="px-4 py-3 border-gray-100">{article.slug}</td>
                            <td className="px-4 py-3 border-gray-100">{(article.tags || []).join(', ')}</td>
                            <td className="px-4 py-3 border-gray-100">{article.status}</td>
                            <td className="px-4 py-3 border-gray-100">
                                {article.createdAt ? new Date(article.createdAt).toLocaleDateString('vi-VN') : '-'}
                            </td>
                            <ArticleTableActions article={article}/>
                        </tr>
                    ))
                )}
                </tbody>
            </table>
            <div className={'self-end'}>
                <CustomPagination totalPages={articlesData.meta.totalPages}/>
            </div>
        </main>
    );
}
