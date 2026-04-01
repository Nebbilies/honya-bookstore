'use client';

import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import ShortField from "@/components/Input Field/ShortField";
import LongField from "@/components/Input Field/LongField";
import Button from "@/components/Button";
import { Category } from "@/types/types";
import { toast } from "sonner";

const categorySchema = z.object({
    name: z.string().min(1, "Name is required"),
    slug: z.string().min(1, "Slug is required"),
    description: z.string().optional(),
});

type CategoryFormValues = z.infer<typeof categorySchema>;

interface CategoryFormProps {
    mode: 'add' | 'edit';
    categoryId?: string;
    initialData?: Category;
}

export default function CategoryForm({ mode, categoryId, initialData }: CategoryFormProps) {
    const { data: session } = useSession();
    const router = useRouter();

    const form = useForm<CategoryFormValues>({
        resolver: zodResolver(categorySchema),
        defaultValues: {
            name: "",
            slug: "",
            description: "",
        },
    });

    const {control, handleSubmit, reset, formState: { errors, isSubmitting, isValid }} = form;

    useEffect(() => {
        if (mode === 'edit' && initialData) {
            reset({
                name: initialData.name || '',
                slug: initialData.slug || '',
                description: initialData.description || '',
            });
        }
    }, [mode, initialData, reset]);

    const onSubmit = async (data: CategoryFormValues) => {
        try {
            const url = mode === 'add'
                ? `${process.env.NEXT_PUBLIC_API_URL}/categories`
                : `${process.env.NEXT_PUBLIC_API_URL}/categories/${categoryId}`;

            const method = mode === 'add' ? 'POST' : 'PATCH';

            const res = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session?.accessToken}`,
                },
                body: JSON.stringify(data),
            });

            if (!res.ok) {
                const errorData = await res.json();
                toast.error(errorData.message || 'Failed to save category');
                throw new Error(errorData.message || 'Failed to save category');
            }

            toast.success(`Category ${mode === 'add' ? 'added' : 'updated'} successfully`);
            if (mode === 'add') {
                reset();
                router.refresh();
            } else {
                router.push('/admin/categories');
                router.refresh();
            }

        } catch (error) {
            console.error(error);
        }
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)}
              className={`flex flex-col border-1 border-line-color rounded-[15px] px-[25px] py-[15px] ${mode === 'add' ? 'w-2/5' : 'w-full'}`}>
            <div className="flex flex-col gap-4">
                <Controller
                    name="name"
                    control={control}
                    render={({ field, fieldState }) => (
                        <ShortField
                            label={'Category Name'}
                            className={'w-full p-3'}
                            placeholder={'Enter category name'}
                            value={field.value || ''}
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
                            label={'Category Slug'}
                            className={'w-full p-3'}
                            placeholder={'Enter category slug'}
                            value={field.value || ''}
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
                            label={'Description'}
                            className={'w-full p-3 h-64'}
                            charLimit={300}
                            value={field.value || ''}
                            onValueChange={field.onChange}
                            placeholder={'Enter description'}
                        />
                    )}
                />
            </div>
            <Button
                shape={'rect'}
                variant={'solid'}
                icon={mode === 'add' ? 'add' : 'edit'}
                iconSize={25}
                disabled={isSubmitting}
                className={'text-white bg-button-blue hover:bg-sky-600 w-fit h-[50px] font-plus-jakarta-sans rounded-[15px] mt-4'}
            >
                {isSubmitting ? 'Saving...' : (mode === 'add' ? 'Add Category' : 'Update Category')}
            </Button>
        </form>
    );
}