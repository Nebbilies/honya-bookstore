'use client';

import Icon from "@/components/Icon";
import { useRouter } from "next/navigation";
import { useSession } from "next-auth/react";
import { toast } from "sonner";
import {Category} from "@/types/types";

interface CategoryTableOptionsProps {
    category: Category; // hoặc number tùy DB
}

export default function CategoryTableOptions({ category }: CategoryTableOptionsProps) {
    const router = useRouter();
    const session = useSession();
    const categoryId = category.id;

    const handleEdit = () => {
        router.push(`/admin/categories/edit/?slug=${category.slug}`);
    };

    const handleDelete = async () => {
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/categories/${categoryId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session.data?.accessToken}`,
                },
            });

            if (!res.ok) {
                const err = await res.json();
                toast.error(err.message || "Failed to delete category");
                throw new Error('Failed to delete category');
            }

            toast.success("Category deleted successfully");
            router.refresh();
        } catch (error) {
            console.error(error);
        }
    };
    return (
        <div className="flex justify-center gap-3">
            <button onClick={handleEdit} className="text-gray-600 hover:text-blue-400 ml-3">
                <Icon name={'edit'} size={25}/>
            </button>
            <button onClick={handleDelete} className="text-red-500 hover:text-red-700 ml-3">
                <Icon name={'trash'} size={25}/>
            </button>
        </div>
    );
}