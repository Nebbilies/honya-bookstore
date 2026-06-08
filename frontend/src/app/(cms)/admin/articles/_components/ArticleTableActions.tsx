'use client';

import Icon from "@/components/Icon";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { useSession } from "next-auth/react";
import { Article } from "@/types/types";
import { useConfirmation } from "@/app/(cms)/_context/AlertDialogContext";

interface ArticleTableActionsProps {
    article: Article;
}

export default function ArticleTableActions({ article }: ArticleTableActionsProps) {
    const router = useRouter();
    const session = useSession();
    const { openConfirmation } = useConfirmation();

    const handleEdit = () => {
        router.push('/admin/articles/edit?id=' + article.id);
    };

    const handleDelete = async () => {
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/articles/${article.id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session.data?.accessToken}`,
                },
            });

            if (!res.ok) {
                const err = await res.json();
                toast.error(err.message || "Failed to delete article");
                throw new Error('Failed to delete article');
            }

            toast.success("Article deleted successfully");
            router.refresh();
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <td className="px-4 py-3 text-center">
            <div className="flex justify-center gap-3">
                <button onClick={handleEdit} className="text-gray-600 hover:text-blue-400 cursor-pointer">
                    <Icon name={'edit'} size={25}/>
                </button>
                <button onClick={() => openConfirmation({
                    title: 'Delete Article',
                    description: `Are you sure you want to delete the article "${article.title}"? This action cannot be undone.`,
                    onAction: handleDelete
                })} className="text-red-500 hover:text-red-700 cursor-pointer">
                    <Icon name={'trash'} size={25}/>
                </button>
            </div>
        </td>
    );
}
