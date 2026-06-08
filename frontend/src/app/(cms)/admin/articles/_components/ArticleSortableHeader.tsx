'use client';

import { usePathname, useRouter, useSearchParams } from "next/navigation";
import Icon from "@/components/Icon";

interface ArticleSortableHeaderProps {
    column: string;
    label: string;
    className?: string;
}

export default function ArticleSortableHeader({ column, label, className }: ArticleSortableHeaderProps) {
    const searchParams = useSearchParams();
    const pathname = usePathname();
    const { replace } = useRouter();

    const activeSort = searchParams.get('sort');
    const activeOrder = searchParams.get('order') || 'asc';
    const isActive = activeSort === column;

    const handleSort = () => {
        const params = new URLSearchParams(searchParams);
        const nextOrder = isActive && activeOrder === 'asc' ? 'desc' : 'asc';
        params.set('sort', column);
        params.set('order', nextOrder);
        params.delete('page');
        replace(`${pathname}?${params.toString()}`);
    };

    return (
        <th className={`px-4 py-3 border-r border-gray-200 ${className || ''}`}>
            <button onClick={handleSort} className="flex items-center gap-1 font-bold text-gray-700 cursor-pointer hover:text-black">
                {label}
                <Icon name="sort" size={16} className={isActive ? 'text-black' : 'text-gray-400'}/>
            </button>
        </th>
    );
}
