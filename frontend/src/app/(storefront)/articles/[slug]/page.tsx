import { Metadata } from "next";
import { notFound } from "next/navigation";
import Image from "next/image";
import Icon from "@/components/Icon";
import { Article, ArticleResponse } from "@/types/types";
import Breadcrumb, { BreadcrumbItemType } from "@/app/(storefront)/_components/Breadcrumb/Breadcrumb";
import SectionTitle from "@/app/(storefront)/_components/SectionTitle";
import ArticleCard from "@/app/(storefront)/_components/Article/ArticleCard";
import TableOfContents from "@/app/(storefront)/_components/Article/TableOfContents";
import ShareButtons from "@/app/(storefront)/_components/Article/ShareButtons";
import { formatArticleDate, getExcerpt, parseArticleContent } from "@/lib/article-utils";

export const dynamic = 'force-dynamic';

async function getArticle(slug: string): Promise<Article | null> {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    const res = await fetch(`${apiBaseUrl}/articles/public/${slug}`, { cache: 'no-store' });
    if (!res.ok) return null;
    return res.json();
}

async function getRecommended(slug: string): Promise<Article[]> {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    const res = await fetch(`${apiBaseUrl}/articles/public?page=1&limit=4`, { cache: 'no-store' });
    if (!res.ok) return [];
    const data: ArticleResponse = await res.json();
    return data.data.filter((article) => article.slug !== slug).slice(0, 3);
}

export async function generateMetadata({ params }: { params: Promise<{ slug: string }> }): Promise<Metadata> {
    const { slug } = await params;
    const article = await getArticle(slug);
    if (!article) return { title: 'Article' };
    return {
        title: article.title,
        description: getExcerpt(article.content),
    };
}

export default async function ArticlePage({ params }: { params: Promise<{ slug: string }> }) {
    const { slug } = await params;
    const article = await getArticle(slug);
    if (!article) notFound();

    const recommended = await getRecommended(slug);
    const { html, toc } = parseArticleContent(article.content);

    const breadcrumbItems: BreadcrumbItemType[] = [
        { label: 'Articles', href: '/articles' },
        { label: article.title, href: `/articles/${slug}` },
    ];

    return (
        <main className={'flex flex-col w-full max-w-[1100px] mx-auto px-5 pt-4 pb-20 gap-10'}>
            <Breadcrumb items={breadcrumbItems}/>

            <div className={'flex gap-10 justify-center'}>
                {toc.length > 0 && (
                    <aside className={'hidden lg:block w-[185px] shrink-0'}>
                        <TableOfContents items={toc}/>
                    </aside>
                )}

                <article className={'w-full max-w-[720px]'}>
                    <div className={'relative w-full aspect-[720/405] overflow-hidden rounded-[20px] bg-[#efeee8]'}>
                        <Image
                            src={article.thumbnailUrl || '/images/fallbackBookImage.png'}
                            alt={article.title}
                            fill
                            priority
                            sizes={'(max-width: 768px) 100vw, 720px'}
                            className={'object-cover'}
                        />
                    </div>

                    <div className={'flex items-center gap-2 mt-6 text-black'}>
                        <Icon name={'calendar'} size={20}/>
                        <span className={'font-plus-jakarta-sans text-[16px]'}>{formatArticleDate(article.createdAt)}</span>
                    </div>
                    <h1 className={'font-prata text-[28px] md:text-[31px] leading-[1.2] mt-3'}>{article.title}</h1>
                    <hr className={'border-line-color mt-5'}/>

                    <div
                        className={'prose prose-neutral max-w-none mt-6 ' +
                            'prose-headings:font-prata prose-headings:font-normal prose-headings:text-black ' +
                            'prose-h2:text-[25px] prose-h2:mt-8 prose-h2:mb-3 ' +
                            'prose-h3:text-[20px] prose-h3:mt-6 prose-h3:mb-2 ' +
                            'prose-p:font-plus-jakarta-sans prose-p:text-[15px] prose-p:leading-[1.7] prose-p:text-black/80 ' +
                            'prose-li:font-plus-jakarta-sans prose-li:text-[15px] prose-li:text-black/80 ' +
                            'prose-blockquote:font-plus-jakarta-sans prose-blockquote:border-price-color prose-blockquote:text-black/70 ' +
                            'prose-a:text-blue-600 prose-img:rounded-[10px] ' +
                            '[&_h1]:scroll-mt-28 [&_h2]:scroll-mt-28 [&_h3]:scroll-mt-28'}
                        dangerouslySetInnerHTML={{ __html: html }}
                    />

                    <div className={'mt-10'}>
                        <ShareButtons title={article.title}/>
                    </div>
                </article>
            </div>

            {recommended.length > 0 && (
                <section className={'flex flex-col gap-8 mt-6'}>
                    <SectionTitle title={'Recommended Articles'}/>
                    <div className={'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-x-[30px] gap-y-10'}>
                        {recommended.map((item) => (
                            <ArticleCard key={item.id} article={item}/>
                        ))}
                    </div>
                </section>
            )}
        </main>
    );
}
