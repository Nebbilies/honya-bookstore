'use client';

import { useEffect, useState } from "react";
import { TocItem } from "@/lib/article-utils";

export default function TableOfContents({ items }: { items: TocItem[] }) {
    const [activeId, setActiveId] = useState<string>('');

    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) setActiveId(entry.target.id);
                });
            },
            { rootMargin: '0px 0px -70% 0px', threshold: 0 },
        );
        items.forEach((item) => {
            const el = document.getElementById(item.id);
            if (el) observer.observe(el);
        });
        return () => observer.disconnect();
    }, [items]);

    const handleClick = (e: React.MouseEvent, id: string) => {
        e.preventDefault();
        const el = document.getElementById(id);
        if (el) {
            window.history.replaceState(null, '', `#${id}`);
            el.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    };

    return (
        <nav className={'sticky top-28'}>
            <p className={'font-prata text-[16px] uppercase text-black/60 mb-4'}>Table Of Contents</p>
            <ul className={'flex flex-col gap-2.5'}>
                {items.map((item) => (
                    <li key={item.id} style={{ paddingLeft: (item.level - 1) * 10 }}>
                        <a
                            href={`#${item.id}`}
                            onClick={(e) => handleClick(e, item.id)}
                            className={`block font-plus-jakarta-sans text-[13px] leading-[1.3] transition-colors duration-200 ${
                                activeId === item.id ? 'text-blue-600 font-medium' : 'text-black/80 hover:text-black'
                            }`}
                        >
                            {item.text}
                        </a>
                    </li>
                ))}
            </ul>
        </nav>
    );
}
