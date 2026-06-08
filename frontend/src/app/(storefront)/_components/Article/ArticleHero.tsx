import Link from "next/link";
import Image from "next/image";
import Icon from "@/components/Icon";
import { Article } from "@/types/types";
import { getExcerpt, formatArticleDate } from "@/lib/article-utils";

export default function ArticleHero({ article }: { article: Article }) {
    return (
        <Link
            href={`/articles/${article.slug}`}
            className={'group relative block w-full h-[440px] md:h-[540px] overflow-hidden rounded-[20px] bg-[#efeee8]'}
        >
            <Image
                src={article.thumbnailUrl || '/images/fallbackBookImage.png'}
                alt={article.title}
                fill
                priority
                sizes={'(max-width: 1024px) 100vw, 960px'}
                className={'object-cover transition-transform duration-500 group-hover:scale-105'}
            />
            <div className={'absolute inset-x-0 bottom-0 bg-black/40 px-[30px] py-[24px] flex flex-col gap-3'}>
                <h2 className={'font-prata text-[24px] md:text-[31px] leading-[1.2] text-white line-clamp-2'}>
                    {article.title}
                </h2>
                <p className={'font-plus-jakarta-sans text-[16px] leading-[1.3] text-white/90 line-clamp-2 max-w-[760px]'}>
                    {getExcerpt(article.content, 180)}
                </p>
                <div className={'flex items-center justify-between gap-4 flex-wrap mt-1'}>
                    <div className={'flex items-center gap-2 text-white'}>
                        <Icon name={'calendar'} size={20} color={'#ffffff'}/>
                        <span className={'font-plus-jakarta-sans text-[16px]'}>{formatArticleDate(article.createdAt)}</span>
                    </div>
                    <div className={'flex items-center gap-2.5'}>
                        {(article.tags || []).slice(0, 3).map((tag) => (
                            <span
                                key={tag}
                                className={'rounded-full border border-white px-[10px] py-[5px] text-[16px] leading-none text-white font-plus-jakarta-sans capitalize'}
                            >
                                {tag}
                            </span>
                        ))}
                    </div>
                </div>
            </div>
        </Link>
    );
}
