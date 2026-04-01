"use client";

import { usePathname, useSearchParams } from "next/navigation";
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
} from "@/components/ui/pagination";

interface CustomPaginationProps {
    totalPages: number;
    className?: string;
    currentPage?: number;
    onPageChange?: (page: number) => void;
}

export function CustomClientSidePagination({
                                     totalPages,
                                     className,
                                     currentPage: propCurrentPage,
                                     onPageChange,
                                 }: CustomPaginationProps) {
    const pathname = usePathname();
    const searchParams = useSearchParams();

    const currentPage = propCurrentPage || Number(searchParams.get("page")) || 1;

    const handlePageChange = (e: React.MouseEvent, pageNumber: number) => {
        if (onPageChange) {
            e.preventDefault();
            onPageChange(pageNumber);
        }
    };

    const createPageURL = (pageNumber: number | string) => {
        if (onPageChange) return "#";

        const params = new URLSearchParams(searchParams);
        params.set("page", pageNumber.toString());
        return `${pathname}?${params.toString()}`;
    };

    const allPages = generatePagination(currentPage, totalPages);

    return (
        <Pagination className={className}>
            <PaginationContent>
                <PaginationItem>
                    <PaginationLink
                        href={createPageURL(currentPage - 1)}
                        onClick={(e) => handlePageChange(e, currentPage - 1)}
                        aria-disabled={currentPage <= 1}
                        tabIndex={currentPage <= 1 ? -1 : undefined}
                        className={`${currentPage <= 1 ? "pointer-events-none opacity-50" : ""} w-fit px-2`}
                    >
                        &#171;
                    </PaginationLink>
                </PaginationItem>
                {allPages.map((page, index) => {
                    if (page === "...") {
                        return (
                            <PaginationItem key={`ellipsis-${index}`}>
                                <PaginationEllipsis />
                            </PaginationItem>
                        );
                    }
                    return (
                        <PaginationItem key={page}>
                            <PaginationLink
                                href={createPageURL(page)}
                                onClick={(e) => handlePageChange(e, Number(page))}
                                isActive={currentPage === page}
                            >
                                {page}
                            </PaginationLink>
                        </PaginationItem>
                    );
                })}
                <PaginationItem>
                    <PaginationLink
                        href={createPageURL(currentPage + 1)}
                        onClick={(e) => handlePageChange(e, currentPage + 1)}
                        aria-disabled={currentPage >= totalPages}
                        tabIndex={currentPage >= totalPages ? -1 : undefined}
                        className={`${currentPage >= totalPages ? "pointer-events-none opacity-50" : ""} w-fit px-2`}
                    >
                        &#187;
                    </PaginationLink>
                </PaginationItem>
            </PaginationContent>
        </Pagination>
    );
}

function generatePagination(currentPage: number, totalPages: number) {
    if (totalPages <= 7) {
        return Array.from({ length: totalPages }, (_, i) => i + 1);
    }
    if (currentPage <= 2) {
        return [1, 2, 3, "...", totalPages - 1, totalPages];
    }
    if (currentPage === 3) {
        return [1, 2, 3, 4, "...", totalPages - 1, totalPages];
    }
    if (currentPage >= totalPages - 2) {
        return [1, 2, "...", totalPages - 2, totalPages - 1, totalPages];
    }

    return [
        1,
        "...",
        currentPage - 1,
        currentPage,
        currentPage + 1,
        "...",
        totalPages,
    ];
}