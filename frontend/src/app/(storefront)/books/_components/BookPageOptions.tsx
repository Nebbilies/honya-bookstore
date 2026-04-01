'use client'

import Dropbox from "@/components/Input Field/Dropbox";
import {useRouter, usePathname, useSearchParams} from "next/navigation";
import {useState} from "react";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

export default function BookPageOptions() {
    const router = useRouter();
    const pathname = usePathname();
    const searchParams = useSearchParams();



    const getCurrentSortValue = () => {
        const sortPrice = searchParams.get("sort_price");
        const sortRating = searchParams.get("sort_recent");
        if (sortPrice === "asc") return "price_asc";
        if (sortPrice === "desc") return "price_desc";
        if (sortRating === "desc") return "newest";
        if (sortRating === "asc") return "oldest";
        return "default";
    };

    const currentSortValue = getCurrentSortValue();

    const handleChange = (selectedOption: string) => {
        const currentParams = new URLSearchParams(searchParams);
        switch (selectedOption) {
            case "price_asc":
                currentParams.set("sort_price", "asc");
                currentParams.delete("sort_recent");
                break;
            case "price_desc":
                currentParams.set("sort_price", "desc");
                currentParams.delete("sort_recent");
                break;
            case "newest":
                currentParams.set("sort_recent", "desc");
                currentParams.delete("sort_price");
                break;
            case "oldest":
                currentParams.set("sort_recent", "asc");
                currentParams.delete("sort_price");
                break;
            default:
                currentParams.delete("sort_price");
                currentParams.delete("sort_recent");
                break;
        }
        currentParams.delete("page");
        router.push(`${pathname}?${currentParams.toString()}`);
    };

    return (
        <div className={'w-full flex flex-col'}>
            <div className={'flex gap-10'}>
                <Select value={currentSortValue} onValueChange={handleChange}>
                    <SelectTrigger className={"w-full !h-[50px] !bg-white rounded-[10px] border border-gray-200"}>
                        <SelectValue placeholder="Sắp xếp theo" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value={"default"}>Default</SelectItem>
                        <SelectItem value={"price_asc"}>Price: Low to High</SelectItem>
                        <SelectItem value={"price_desc"}>Price: High to Low</SelectItem>
                        <SelectItem value={"newest"}>Newest</SelectItem>
                        <SelectItem value={"oldest"}>Oldest</SelectItem>
                    </SelectContent>
                </Select>
            </div>
        </div>
    )
}