'use client';

import { useForm, useFieldArray, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import ShortField from "@/components/Input Field/ShortField";
import LongField from "@/components/Input Field/LongField";
import Button from "@/components/Button";
import {Book, Category} from "@/types/types";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import SelectMediaDialog from "@/app/(cms)/admin/media/upload/_components/SelectMediaDialog";
import {useEffect, useState} from "react";
import Image from "next/image";
import {useSession} from "next-auth/react";
import {toast} from "sonner";
import {useRouter} from "next/navigation";

const imageSchema = z.object({
    isCover: z.boolean(),
    mediaId: z.string().min(1, "Image is required"),
    url: z.string().url(),
    altText: z.string().optional(),
});

const bookSchema = z.object({
    title: z.string().min(1, "Required field"),
    description: z.string().optional(),
    author: z.string().min(1, "Required field"),
    price: z.coerce.number<number>({
        error: "Invalid data",
    }).min(0, "Invalid data"),
    pageCount: z.coerce.number<number>({
        error: "Invalid data",
    }).int().min(1, "Must be at least 1"),
    publishedYear: z.coerce.number<number>().int().min(1000, "Invalid year").max(new Date().getFullYear(), "Invalid year"),
    publisher: z.string().min(1, "Required field"),
    weight: z.coerce.number<number>('Invalid data').min(0).optional(),
    categories: z.array(z.object({
        categoryId: z.string(),
    })).min(1, "Please select at least one category"),
    images: z.array(imageSchema)
        .min(1, "At least one image is required")
        .refine((images) => images.some(img => img.isCover), {
            message: "A cover image is required",
        })
});

type BookFormValues = z.infer<typeof bookSchema>;

export type BookImageValues = z.infer<typeof imageSchema>;

export type CategoryItemValues = {
    categoryId: string;
}

interface AddBookFormProps {
    categories: Category[];
    initialData?: Book;
    mode: 'add' | 'edit';
}

export default function BookForm({ categories, mode, initialData }: AddBookFormProps) {
    const session = useSession();
    const router = useRouter();

    const [isMediaDialogOpen, setIsMediaDialogOpen] = useState(false);
    const [currentImageType, setCurrentImageType] = useState<'cover' | 'gallery'>('gallery');

    const onOpenMediaDialog = (imageType: 'cover' | 'gallery') => {
        setCurrentImageType(imageType);
        setIsMediaDialogOpen(true);
    }

    const onCloseMediaDialog = () => {
        setIsMediaDialogOpen(false);
    }

    const form = useForm<BookFormValues>({
        resolver: zodResolver(bookSchema),
        defaultValues: {
            title: "",
            description: "",
            author: "",
            price: 0,
            pageCount: 0,
            publishedYear: new Date().getFullYear(),
            publisher: "",
            weight: 0,
            categories: [],
            images: [],
        },
        mode: "onChange"
    });

    const { control, handleSubmit, reset, formState: { errors } } = form;

    const {
        fields: categoryFields,
        append: appendCategory,
        remove: removeCategory
    } = useFieldArray({
        control,
        name: "categories",
    });

    const {
        fields: imageFields,
        append: appendImage,
        remove: removeImage
    } = useFieldArray({
        control,
        name: "images"
    });

    useEffect(() => {
        if (mode === 'edit' && initialData) {
            reset({
                title: initialData.title || '',
                description: initialData.description || '',
                author: initialData.author || '',
                price: initialData.price || 0,
                pageCount: initialData.pagesCount || 0,
                publishedYear: initialData.yearPublished || new Date().getFullYear(),
                publisher: initialData.publisher || '',
                weight: initialData.weight || 0,
                categories: initialData.categories.map(cat => ({
                    categoryId: cat.id,
                })),
                images: initialData.media.map(media => ({
                    isCover: media.isCover,
                    mediaId: media.id,
                    url: media.url,
                    altText: media.altText || '',
                })),
            })
        }
    }, [mode, initialData, reset]);

    const onSubmit = async (data: BookFormValues) => {
        const method = mode === 'add' ? 'POST' : 'PATCH';
        const url = mode === 'add'
            ? `${process.env.NEXT_PUBLIC_API_URL}/books`
            : `${process.env.NEXT_PUBLIC_API_URL}/books/${initialData?.id}`;

        const res = await fetch(url, {
            method: method,
            headers: {
                authorization: `Bearer ${session.data?.accessToken}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                author: data.author,
                categoryIds: data.categories.map(cat => cat.categoryId),
                description: data.description,
                pagesCount: data.pageCount,
                price: data.price,
                yearPublished: data.publishedYear,
                publisher: data.publisher,
                title: data.title,
                weight: data.weight,
                stockQuantity: 1,
                media: data.images.map(img => {
                    return {
                        mediaId: img.mediaId,
                        isCover: img.isCover,
                    }
                }),
            })
        })

        if (!res.ok) {
            const err = await res.json();
            toast.error(err.message || `Failed to ${mode === 'add' ? 'add' : 'update'} book`);
            throw new Error(err.message || 'Failed to save book');
        }

        toast.success(`Book ${mode === 'add' ? 'added' : 'updated'} successfully`);
        router.push('/admin/books');
        router.refresh();
    };

    return (
        <form id={'add-book-form'} onSubmit={handleSubmit(onSubmit)} className="w-full flex flex-col gap-6">
            <section className={'flex gap-8 rounded-[15px] w-full px-[25px] py-[15px] border-1 border-line-color justify-between'}>
                <div className={'flex flex-col justify-between w-3/5 gap-4 h-fit'}>
                    <Controller
                        name="title"
                        control={control}
                        render={({ field, fieldState }) => (
                            <ShortField
                                label={'Book Title'}
                                className={'w-full p-3'}
                                placeholder={'Enter book title'}
                                value={field.value}
                                onValueChange={field.onChange}
                                required={true}
                                error={fieldState.error?.message}
                            />
                        )}
                    />

                    <Controller
                        name="description"
                        control={control}
                        render={({ field }) => (
                            <LongField
                                label={'Book Description'}
                                className={'w-full h-50 p-3'}
                                charLimit={1000}
                                value={field.value || ''}
                                onValueChange={field.onChange}
                                placeholder={'Enter book description'}
                            />
                        )}
                    />
                </div>
                <div className={'flex flex-col w-2/5 justify-between gap-4'}>
                    <Controller
                        name="author"
                        control={control}
                        render={({ field, fieldState }) => (
                            <ShortField
                                label={'Book Author'}
                                className={'w-full p-3'}
                                value={field.value}
                                onValueChange={field.onChange}
                                required={true}
                                placeholder={'Enter book author'}
                                error={fieldState.error?.message}
                            />
                        )}
                    />

                    <Controller
                        name="price"
                        control={control}
                        render={({ field, fieldState }) => (
                            <ShortField
                                label={'Price'}
                                type={'number'}
                                className={'w-full p-3'}
                                value={field.value === 0 ? '' : field.value.toString()}
                                onValueChange={field.onChange}
                                required={true}
                                placeholder={'Enter book price'}
                                error={fieldState.error?.message}
                            />
                        )}
                    />

                    <div className={'flex gap-10 justify-between'}>
                        <Controller
                            name="pageCount"
                            control={control}
                            render={({ field, fieldState }) => (
                                <ShortField
                                    label={'Pages Count'}
                                    type={'number'}
                                    className={'w-full p-3'}
                                    value={field.value === 0 ? '' : field.value.toString()}
                                    onValueChange={field.onChange}
                                    required={true}
                                    placeholder={'Enter pages count'}
                                    error={fieldState.error?.message}
                                />
                            )}
                        />

                        <Controller
                            name="publishedYear"
                            control={control}
                            render={({ field, fieldState }) => (
                                <ShortField
                                    label={'Year Published'}
                                    type={'number'}
                                    className={'w-full p-3'}
                                    value={field.value === 0 ? '' : field.value.toString()}
                                    onValueChange={field.onChange}
                                    required={true}
                                    placeholder={'Enter year published'}
                                    error={fieldState.error?.message}
                                />
                            )}
                        />
                    </div>
                </div>
            </section>

            <section className={'flex gap-8 rounded-[15px] w-full px-[25px] py-[15px] border-1 border-line-color justify-between'}>
                <div className={'flex flex-col w-full gap-4'}>
                    <Controller
                        name="categories"
                        control={control}
                        render={() => (
                            <Select onValueChange={(value) => {
                                const selectedCategory = categories.find(cat => cat.id === value);
                                if (selectedCategory) {
                                    const isAdded = categoryFields.some(cat => cat.categoryId === selectedCategory.id);
                                    if (!isAdded) {
                                        appendCategory({
                                            categoryId: value,
                                        });
                                    }
                                }
                            }}>
                                <SelectTrigger className={'w-full'}>
                                    <SelectValue placeholder="Select Categories" />
                                </SelectTrigger>
                                <SelectContent>
                                    {categories.map((category) => (
                                        <SelectItem key={category.id} value={category.id}>
                                            {category.name}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        )}/>

                    <label className={'flex flex-col w-full'}>Added Categories</label>
                    <div className={'flex flex-wrap gap-2'}>
                        {categoryFields.length === 0 && (
                            <span className={'text-red-500 opacity-80 text-sm'}>
                                {errors.categories?.message || "Please select a category"}
                            </span>
                        )}
                        {categoryFields.map((field, index) => (
                            <div key={field.id}
                                 className={'bg-gray-200 text-black px-3 py-1 rounded-full flex items-center gap-2'}>
                                <span>
                                    {categories.find((cat) => cat.id === field.categoryId)?.name || 'Unknown Category'}
                                </span>
                                <button
                                    type="button"
                                    onClick={() => removeCategory(index)}
                                    className={'text-red-500 hover:text-red-700'}
                                >
                                    &times;
                                </button>
                            </div>
                        ))}
                    </div>
                </div>

                <Controller
                    name="publisher"
                    control={control}
                    render={({ field, fieldState }) => (
                        <ShortField
                            label={'Publisher'}
                            className={'w-full p-3'}
                            value={field.value}
                            required={true}
                            onValueChange={field.onChange}
                            placeholder={'Enter book publisher'}
                            error={fieldState.error?.message}
                        />
                    )}
                />

                <Controller
                    name="weight"
                    control={control}
                    render={({ field, fieldState }) => (
                        <ShortField
                            label={'Weight'}
                            type={'number'}
                            className={'w-full p-3'}
                            value={field.value === 0 ? '' : field.value?.toString()}
                            onValueChange={field.onChange}
                            placeholder={'Enter book weight (grams)'}
                            error={fieldState.error?.message}
                        />
                    )}
                />
            </section>

            <section className={'flex gap-8 rounded-[15px] w-full px-[25px] py-[15px] border-1 border-line-color justify-between'}>
                <div className={'flex flex-col gap-4 w-full'}>
                    <label className={'text-[16px] text-black font-plus-jakarta-sans'}>Cover Image</label>
                    <div className={'relative h-60 border border-line-color rounded-[10px] flex items-center justify-center text-gray-500 w-full'}>
                        {imageFields.find(img => img.isCover) ? (
                            <Image src={imageFields.find(img => img.isCover)!.url}
                                   alt={imageFields.find(img => img.isCover)!.altText || 'Cover Image'}
                                   fill
                                   className="object-contain rounded-md"/>
                        ) : errors.images ? (
                            <span className={'text-red-500 opacity-80 text-sm'}>
                                {errors.images.message || "Please select a cover image"}
                            </span>
                        ) : "No cover image selected"}
                    </div>
                    <button onClick={() => {
                        onOpenMediaDialog('cover');
                    }}
                            type="button"
                            className={'border-1 cursor-pointer border-button-blue hover:bg-gray-200 text-black bg-transparent w-full h-[50px] rounded-[15px]'}>
                        Select Cover
                    </button>
                </div>

                <div className={'flex flex-col gap-4 w-full'}>
                    <label className={'text-[16px] text-black font-plus-jakarta-sans'}>Images</label>
                    <button
                        type="button"
                        onClick={() => {
                            onOpenMediaDialog('gallery');
                        }}
                        className={'text-white bg-button-blue hover:bg-sky-600 w-full h-[50px] rounded-[15px]'}
                    >
                        Add Images
                    </button>
                </div>

                <div className={'flex flex-col gap-4 w-full'}>
                    <label className={'text-[16px] text-black font-plus-jakarta-sans'}>Added Images</label>
                    <div className={'h-60 flex-wrap rounded-[10px] flex text-gray-500 w-full items-start overflow-y-scroll'}>
                        {imageFields.length === 0 ? "No images added" : (
                            imageFields.filter((field) => !field.isCover)
                                .map((field, index) => (
                                    <Image src={field.url} alt={field.altText ? field.altText : `Book Image ${index}`} key={field.id} width={100} height={100} className="object-contain rounded-md mr-2 mb-2"/>
                                ))
                        )}
                    </div>
                </div>
            </section>

            <Button
                type={'submit'}
                form={'add-book-form'}
                shape={'rect'}
                variant={'solid'}
                icon={'add'}
                iconSize={25}
                disabled={false}
                className={'text-white bg-button-blue hover:bg-sky-600 w-fit h-[50px] font-plus-jakarta-sans rounded-[15px]'}
            >
                {mode === 'add' ? 'Add Book' : 'Update Book'}
            </Button>
            {isMediaDialogOpen && (
                <SelectMediaDialog
                    onClose={onCloseMediaDialog}
                    isOpen={isMediaDialogOpen}
                    imageType={currentImageType}
                    appendImage={appendImage}
                    removeImage={(mediaId) => {
                        const index = imageFields.findIndex(img => img.mediaId === mediaId);
                        if (index !== -1) {
                            removeImage(index);
                        }
                    }}
                    currentImages={imageFields.filter(img => !img.isCover)}
                    currentCoverImage={imageFields.find(img => img.isCover)}
                />
            )}
        </form>
    )
}