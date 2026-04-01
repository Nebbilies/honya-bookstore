'use client';
import { usePathname } from "next/navigation";
import { BreadcrumbSegment } from "@/lib/breadcrumb-utils";
import Link from "next/link";

export interface BreadcrumbItemType {
    label: string;
    href: string;
}

interface BreadcrumbProps {
    items: BreadcrumbItemType[];
    separator?: React.ReactNode;
}

export default function Breadcrumb({ items }: BreadcrumbProps) {
    const breadcrumbs: BreadcrumbSegment[] = generateBreadcrumbs(items);
    breadcrumbs.unshift(
        { name: 'Home', href: '/' }
    )
    return (
        <nav aria-label={'breadcrumb'} className={'flex items-center py-1 text-[16px] font-plus-jakarta-sans gap-2'}>
            {breadcrumbs.map((breadcrumb, index) => (
                <div key={index} className={'flex items-center gap-2'}>
                    <Link href={breadcrumb.href} className={'hover:text-blue-600 transition-all duration-200'}>
                    {breadcrumb.name}
                    </Link>
                    {index !== breadcrumbs.length - 1 && (
                        "/"
                    )}
                </div>
            ))}
        </nav>
    )
}

 const generateBreadcrumbs = (items: BreadcrumbItemType[]): BreadcrumbSegment[] => {
    return items.map((item) => ({
        name: item.label,
        href: item.href,
    })
    );
 }