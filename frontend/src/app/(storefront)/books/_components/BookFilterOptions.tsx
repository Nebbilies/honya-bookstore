'use client';

import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useRouter, usePathname, useSearchParams } from "next/navigation";
import { useEffect } from "react";
import ShortField from "@/components/Input Field/ShortField";
import Button from "@/components/Button";
import { Category } from "@/types/types";
import Icon from "@/components/Icon";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import BookPageOptions from "@/app/(storefront)/books/_components/BookPageOptions";

const filterSchema = z.object({
    search: z.string().optional(),
    categoryId: z.string().optional(),
    publisher: z.string().optional(),
    year: z.string().refine((val) => !val || !isNaN(Number(val)), {
        message: "Must be a number",
    }).optional(),
    minPrice: z.string().refine((val) => !val || !isNaN(Number(val)), {
        message: "Must be a number",
    }).optional(),
    maxPrice: z.string().refine((val) => !val || !isNaN(Number(val)), {
        message: "Must be a number",
    }).optional(),
});

type FilterFormValues = z.infer<typeof filterSchema>;

interface BookFilterOptionsProps {
    categories: Category[];
}

export default function BookFilterOptions({ categories }: BookFilterOptionsProps) {
    const router = useRouter();
    const pathname = usePathname();
    const searchParams = useSearchParams();

    const form = useForm<FilterFormValues>({
        resolver: zodResolver(filterSchema),
        defaultValues: {
            search: searchParams.get('search') || '',
            categoryId: searchParams.get('category_ids') || '',
            publisher: searchParams.get('publisher') || '',
            year: searchParams.get('year') || '',
            minPrice: searchParams.get('min_price') || '',
            maxPrice: searchParams.get('max_price') || '',
        },
    });

    const { control, handleSubmit, reset } = form;

    useEffect(() => {
        reset({
            search: searchParams.get('search') || '',
            categoryId: searchParams.get('category_ids') || '',
            publisher: searchParams.get('publisher') || '',
            year: searchParams.get('year') || '',
            minPrice: searchParams.get('min_price') || '',
            maxPrice: searchParams.get('max_price') || '',
        });
    }, [searchParams, reset]);

    const onSubmit = (data: FilterFormValues) => {
        const params = new URLSearchParams(searchParams);
        params.set('page', '1');
        const updateParam = (key: string, value?: string) => {
            if (value && value.trim() !== '') {
                params.set(key, value);
            } else {
                params.delete(key);
            }
        };
        updateParam('search', data.search);
        updateParam('category_ids', data.categoryId);
        updateParam('publisher', data.publisher);
        updateParam('year', data.year);
        updateParam('min_price', data.minPrice);
        updateParam('max_price', data.maxPrice);
        router.replace(`${pathname}?${params.toString()}`, { scroll: false });
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="w-full">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                <div className="relative">
                    <Controller
                        name="search"
                        control={control}
                        render={({ field }) => (
                            <div className="relative">
                                <ShortField
                                    {...field}
                                    value={field.value}
                                    onValueChange={field.onChange}
                                    className="w-full p-3 pl-10 h-[50px] bg-white rounded-[5px] border border-gray-200"
                                    placeholder="Search..."
                                />
                                <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                                    <Icon name="search" size={20} />
                                </div>
                            </div>
                        )}
                    />
                </div>
                <div>
                    <Controller
                        name="categoryId"
                        control={control}
                        render={({ field }) => (
                            <Select value={field.value} onValueChange={field.onChange}>
                                <SelectTrigger className="w-full p-3 !h-[50px] !bg-white rounded-[10px] border border-gray-200">
                                    <SelectValue placeholder="Select Category..." />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem disabled={true} value="none">
                                        Select Category...
                                    </SelectItem>
                                    {categories.map((category) => (
                                        <SelectItem key={category.id} value={category.id.toString()}>
                                            {category.name}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        )}
                    />
                </div>
                <Controller
                    name="publisher"
                    control={control}
                    render={({ field }) => (
                        <ShortField
                            {...field}
                            value={field.value}
                            onValueChange={field.onChange}
                            className="w-full p-3 h-[50px] bg-white rounded-[5px] border border-gray-200"
                            placeholder="Publisher..."
                        />
                    )}
                />
                <Controller
                    name="year"
                    control={control}
                    render={({ field, fieldState }) => (
                        <ShortField
                            {...field}
                            value={field.value}
                            onValueChange={field.onChange}
                            className="w-full p-3 h-[50px] bg-white rounded-[5px] border border-gray-200"
                            placeholder="Year Published..."
                            error={fieldState.error?.message}
                        />
                    )}
                />
                <div className={'flex gap-2'}>
                    <Controller
                        name="minPrice"
                        control={control}
                        render={({ field, fieldState }) => (
                            <ShortField
                                {...field}
                                value={field.value}
                                onValueChange={field.onChange}
                                className="w-full p-3 h-[50px] bg-white rounded-[5px] border border-gray-200"
                                placeholder="Min price..."
                                error={fieldState.error?.message}
                            />
                        )}
                    />
                    <Controller
                        name="maxPrice"
                        control={control}
                        render={({ field, fieldState }) => (
                            <ShortField
                                {...field}
                                value={field.value}
                                onValueChange={field.onChange}
                                className="w-full p-3 h-[50px] bg-white rounded-[5px] border border-gray-200"
                                placeholder="Max price..."
                                error={fieldState.error?.message}
                            />
                        )}
                    />
                </div>
                <BookPageOptions />
            </div>
            <div className="mt-4 flex gap-4">
                <Button
                    type="submit"
                    variant="solid"
                    className="w-[200px] h-[45px] bg-button-blue hover:bg-sky-600 text-white font-plus-jakarta-sans rounded-[5px]"
                >
                    Apply Filter
                </Button>
                <Button
                    type='reset'
                    variant="outline"
                    onClick={() => {
                        reset();
                        router.replace(pathname, { scroll: false });
                    }}
                    className="ml-4 w-[200px] h-[45px] border border-gray-400 text-black font-plus-jakarta-sans rounded-[5px]">
                    Clear Filter
                </Button>
            </div>
        </form>
    );
}