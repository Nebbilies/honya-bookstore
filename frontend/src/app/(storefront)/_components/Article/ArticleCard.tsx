import Link from "next/link";
import Image from "next/image";
import { Article } from "@/types/types";
import { getExcerpt, getCategoryLabel } from "@/lib/article-utils";

export default function ArticleCard({ article }: { article: Article }) {
    const category = getCategoryLabel(article.tags);
    return (
        <Link href={`/articles/${article.slug}`} className={'group flex flex-col gap-3 w-full'}>
            <div className={'relative w-full aspect-[300/169] overflow-hidden rounded-[10px] bg-[#efeee8]'}>
                <Image
                    src={article.thumbnailUrl || '/images/fallbackBookImage.png'}
                    alt={article.title}
                    fill
                    sizes={'(max-width: 1024px) 50vw, 300px'}
                    className={'object-cover transition-transform duration-300 group-hover:scale-105'}
                />
            </div>
            <div className={'flex flex-col gap-2'}>
                {category && (
                    <span className={'font-plus-jakarta-sans text-[11px] font-extrabold tracking-wide uppercase text-black/30'}>
                        {category}
                    </span>
                )}
                <h3 className={'font-prata text-[20px] leading-[1.2] text-black group-hover:text-blue-600 transition-colors duration-200'}>
                    {article.title}
                </h3>
                <p className={'font-plus-jakarta-sans text-[13px] leading-[1.4] text-black/80 line-clamp-2'}>
                    {getExcerpt(article.content)}
                </p>
            </div>
        </Link>
    );
}
