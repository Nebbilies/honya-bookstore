'use client';

import Button from "@/components/Button";
import {useRouter, useSearchParams} from "next/navigation";
import {useSession} from "next-auth/react";
import { toast } from "sonner";
import {useConfirmation} from "@/app/(cms)/_context/AlertDialogContext"; // Giả sử bạn có dùng thư viện toast, nếu không có thể alert

interface MediaTableOptionsProps {
    mediaId: string;
}

export default function MediaTableOptions({ mediaId }: MediaTableOptionsProps) {
    const router = useRouter();
    const { data: session } = useSession();
    const { openConfirmation } = useConfirmation();

    const handleDelete = async () => {
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/media/${mediaId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session?.accessToken}`,
                },
            });

            if (!res.ok) {
                throw new Error('Failed to delete media');
            }

            toast.success("Media deleted successfully");
            router.refresh();
        } catch (error) {
            console.error(error);
            toast.error("Failed to delete media");
        }
    }

    return (
        <div className={'flex flex-col gap-2 items-center justify-center'}>
            <Button
                onClick={() => {
                    openConfirmation({
                        title: 'Delete Media',
                        description: 'Are you sure you want to delete this media? This action cannot be undone.',
                        onAction: handleDelete,
                    });
                }}
                icon={'trash'}
                iconSize={20}
                iconPosition={'left'}
                variant={'outline'}
                className={'py-2 px-4 bg-[#ff0000] text-white hover:bg-red-700 rounded-lg flex items-center gap-2'}
            >
                Delete
            </Button>
        </div>
    );
}