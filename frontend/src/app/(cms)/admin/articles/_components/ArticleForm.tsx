'use client';

import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useEffect, useState } from "react";
import Image from "next/image";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import ShortField from "@/components/Input Field/ShortField";
import Button from "@/components/Button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import RichTextEditor from "@/app/(cms)/admin/articles/_components/RichTextEditor";
import TagInput from "@/app/(cms)/admin/articles/_components/TagInput";
import SelectMediaDialog from "@/app/(cms)/admin/media/upload/_components/SelectMediaDialog";
import { Article } from "@/types/types";

const STATUS_OPTIONS = ['DRAFT', 'PUBLISHED'] as const;

const articleSchema = z.object({
    title: z.string().min(1, "Required field"),
    slug: z.string().optional(),
    content: z.string().refine((html) => html.replace(/<[^>]*>/g, '').trim().length > 0, {
        message: "Content is required",
    }),
    tags: z.array(z.string()).optional(),
    status: z.enum(STATUS_OPTIONS, { error: "Please choose a status" }),
    thumbnailId: z.string().min(1, "A thumbnail is required"),
    thumbnailUrl: z.string().min(1, "A thumbnail is required"),
});

type ArticleFormValues = z.infer<typeof articleSchema>;

function slugify(value: string) {
    return value
        .toLowerCase()
        .trim()
        .replace(/[^a-z0-9\s-]/g, '')
        .replace(/\s+/g, '-')
        .replace(/-+/g, '-');
}

interface ArticleFormProps {
    mode: 'add' | 'edit';
    initialData?: Article;
}

export default function ArticleForm({ mode, initialData }: ArticleFormProps) {
    const session = useSession();
    const router = useRouter();
    const [isThumbnailDialogOpen, setIsThumbnailDialogOpen] = useState(false);

    const form = useForm<ArticleFormValues>({
        resolver: zodResolver(articleSchema),
        defaultValues: {
            title: '',
            slug: '',
            content: '',
            tags: [],
            status: 'DRAFT',
            thumbnailId: '',
            thumbnailUrl: '',
        },
        mode: 'onChange',
    });

    const { control, handleSubmit, reset, setValue, watch, formState: { errors } } = form;
    const thumbnailUrl = watch('thumbnailUrl');

    useEffect(() => {
        if (mode === 'edit' && initialData) {
            reset({
                title: initialData.title || '',
                slug: initialData.slug || '',
                content: initialData.content || '',
                tags: initialData.tags || [],
                status: (STATUS_OPTIONS as readonly string[]).includes(initialData.status) ? initialData.status as ArticleFormValues['status'] : 'DRAFT',
                thumbnailId: initialData.thumbnailId || '',
                thumbnailUrl: initialData.thumbnailUrl || '',
            });
        }
    }, [mode, initialData, reset]);

    const onSubmit = async (data: ArticleFormValues) => {
        const method = mode === 'add' ? 'POST' : 'PUT';
        const url = mode === 'add'
            ? `${process.env.NEXT_PUBLIC_API_URL}/articles`
            : `${process.env.NEXT_PUBLIC_API_URL}/articles/${initialData?.id}`;

        const res = await fetch(url, {
            method,
            headers: {
                authorization: `Bearer ${session.data?.accessToken}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                title: data.title,
                slug: data.slug ? data.slug : slugify(data.title),
                content: data.content,
                tags: data.tags,
                status: data.status,
                thumbnailId: data.thumbnailId,
                thumbnailUrl: data.thumbnailUrl,
            }),
        });

        if (!res.ok) {
            const err = await res.json();
            toast.error(err.message || `Failed to ${mode === 'add' ? 'add' : 'update'} article`);
            throw new Error('Failed to save article');
        }

        toast.success(`Article ${mode === 'add' ? 'added' : 'updated'} successfully`);
        router.push('/admin/articles');
        router.refresh();
    };

    return (
        <form id={'article-form'} onSubmit={handleSubmit(onSubmit)} className="w-full flex flex-col gap-6">
            <section className={'flex gap-8 rounded-[15px] w-full px-[25px] py-[15px] border-1 border-line-color justify-between'}>
                <Controller
                    name="title"
                    control={control}
                    render={({ field, fieldState }) => (
                        <ShortField
                            label={'Article Title'}
                            className={'w-full p-3'}
                            placeholder={'Input Article Title'}
                            value={field.value}
                            onValueChange={field.onChange}
                            required={true}
                            error={fieldState.error?.message}
                        />
                    )}
                />
                <Controller
                    name="slug"
                    control={control}
                    render={({ field, fieldState }) => (
                        <ShortField
                            label={'Slug'}
                            className={'w-full p-3'}
                            placeholder={'Input Slug'}
                            helper={'Automatically generated from Title if left blank'}
                            value={field.value || ''}
                            onValueChange={field.onChange}
                            error={fieldState.error?.message}
                        />
                    )}
                />
            </section>

            <section className={'flex flex-col gap-4 rounded-[15px] w-full px-[25px] py-[15px] border-1 border-line-color'}>
                <label className={'text-[16px] text-black font-plus-jakarta-sans'}>Content</label>
                <Controller
                    name="content"
                    control={control}
                    render={({ field, fieldState }) => (
                        <RichTextEditor
                            value={field.value}
                            onChange={field.onChange}
                            error={fieldState.error?.message}
                        />
                    )}
                />
            </section>

            <section className={'flex gap-8 rounded-[15px] w-full px-[25px] py-[15px] border-1 border-line-color justify-between'}>
                <div className={'flex flex-col gap-4 w-full'}>
                    <label className={'text-[16px] text-black font-plus-jakarta-sans'}>
                        Thumbnail<span className="text-red-500 ml-1">*</span>
                    </label>
                    <div className={'relative h-60 border border-line-color rounded-[10px] flex items-center justify-center text-gray-500 w-full'}>
                        {thumbnailUrl ? (
                            <Image src={thumbnailUrl} alt={'Thumbnail'} fill className="object-contain rounded-md"/>
                        ) : errors.thumbnailId ? (
                            <span className={'text-red-500 opacity-80 text-sm'}>{errors.thumbnailId.message}</span>
                        ) : "No thumbnail selected"}
                    </div>
                    <button
                        type="button"
                        onClick={() => setIsThumbnailDialogOpen(true)}
                        className={'border-1 cursor-pointer border-button-blue hover:bg-gray-200 text-black bg-transparent w-full h-[50px] rounded-[15px]'}
                    >
                        Select Thumbnail
                    </button>
                </div>

                <div className={'flex flex-col gap-4 w-full'}>
                    <Controller
                        name="tags"
                        control={control}
                        render={({ field }) => (
                            <TagInput
                                label={'Tags'}
                                className={'w-full p-3'}
                                placeholder={'Input Tags'}
                                helper={'Type a tag, press "," to add. Letters, numbers, spaces and "-" only. Max 5 tags, under 20 chars each.'}
                                value={field.value || []}
                                onChange={field.onChange}
                            />
                        )}
                    />

                    <Controller
                        name="status"
                        control={control}
                        render={({ field, fieldState }) => (
                            <div className="flex flex-col font-plus-jakarta-sans w-full">
                                <label className="mb-1 text-[16px] text-black">
                                    Status<span className="text-red-500 ml-1">*</span>
                                </label>
                                <Select value={field.value} onValueChange={field.onChange}>
                                    <SelectTrigger className={'w-full'}>
                                        <SelectValue placeholder="Choose status"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        {STATUS_OPTIONS.map((status) => (
                                            <SelectItem key={status} value={status}>{status}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                                {fieldState.error && <span className="text-xs text-red-500 mt-1">{fieldState.error.message}</span>}
                            </div>
                        )}
                    />
                </div>
            </section>

            <Button
                type={'submit'}
                form={'article-form'}
                shape={'rect'}
                variant={'solid'}
                icon={'add'}
                iconSize={25}
                className={'text-white bg-button-blue hover:bg-sky-600 w-fit h-[50px] font-plus-jakarta-sans rounded-[15px]'}
            >
                {mode === 'add' ? 'Publish' : 'Update Article'}
            </Button>

            {isThumbnailDialogOpen && (
                <SelectMediaDialog
                    isOpen={isThumbnailDialogOpen}
                    onClose={() => setIsThumbnailDialogOpen(false)}
                    imageType="cover"
                    appendImage={(img) => {
                        setValue('thumbnailId', img.mediaId, { shouldValidate: true });
                        setValue('thumbnailUrl', img.url, { shouldValidate: true });
                    }}
                    removeImage={(mediaId) => {
                        if (watch('thumbnailId') !== mediaId) return;
                        setValue('thumbnailId', '', { shouldValidate: true });
                        setValue('thumbnailUrl', '', { shouldValidate: true });
                    }}
                    currentCoverImage={thumbnailUrl ? { isCover: true, mediaId: watch('thumbnailId'), url: thumbnailUrl } : undefined}
                />
            )}
        </form>
    );
}
