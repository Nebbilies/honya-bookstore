"use client";

import {Review} from "@/types/types";
import StarIcon from "@/components/Icon/StarIcon";
import ReviewControl from "@/app/(storefront)/books/_components/Review/ReviewControl";
import { Trash2 } from "lucide-react";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useSession } from "next-auth/react";
import { toast } from "sonner";
import { publicApiUrl } from "@/lib/api-url";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

interface ReviewCardProps {
    review: Review;
    canDelete: boolean;
}

// TODO: Replace placeholder author name/avatar once a user profile lookup is available.
export default function ReviewCard({ review, canDelete }: ReviewCardProps) {
    const router = useRouter();
    const session = useSession();
    const [isDeleting, setIsDeleting] = useState(false);

    const initialUserVote = review.userVote === 'UP'
        ? 'upvote'
        : review.userVote === 'DOWN'
            ? 'downvote'
            : null;

    const handleDelete = async () => {
        setIsDeleting(true);

        let res: Response;
        try {
            res = await fetch(publicApiUrl(`/reviews/${review.id}`), {
                method: 'DELETE',
                headers: {
                    authorization: `Bearer ${session.data?.accessToken}`,
                },
            });
        } catch (error) {
            console.error('Failed to delete review:', error);
            toast.error('Could not reach the review service');
            setIsDeleting(false);
            return;
        }

        if (!res.ok) {
            let message = 'Failed to delete review';
            try {
                const data = await res.json();
                message = data.message || data.detail || data.title || message;
            } catch {
                // Keep the generic message when the backend returns a non-JSON error.
            }
            toast.error(message);
            setIsDeleting(false);
            return;
        }

        toast.success('Review deleted');
        router.refresh();
    };

    return (
        <section className={'w-full max-w-[1000px] bg-[#edebe2] rounded-[20px] p-[20px] flex flex-col gap-4'}>
            <div className={'flex gap-4 items-center'}>
                <div className={'w-[60px] h-[60px] rounded-full bg-cover bg-center'}
                        style={{backgroundImage: `url('/images/avatarPlaceholder.png')`}}/>
                <span className={'font-prata text-[22px]'}>{review.authorName ?? 'Anonymous'}</span>
            </div>
            <div className={'flex gap-2 items-end'}>
                <div className={'flex'}>
                    {Array.from({ length: 5 }).map((_, index) => {
                        const starEmptyStyle = 'stroke-[#757575] fill-none';
                        const starFullStyle = 'stroke-[#757575] fill-[#fff7a1]';
                        return (
                            <span key={index}>
                                <StarIcon className={`w-[30px] h-[30px] ${index < review.rating ? starFullStyle : starEmptyStyle}`}/>
                            </span>
                        )
                    })}
                </div>
                <span className={'font-plus-jakarta-sans text-[18px]'}>{review.rating}</span>
            </div>
            <p className={'font-plus-jakarta-sans text-[15px]'}>{review.content}</p>
            <div className={'flex items-center justify-between gap-4'}>
                <ReviewControl reviewId={review.id} initialVoteCount={review.voteCount ?? 0} initialUserVote={initialUserVote}/>
                {canDelete && (
                    <AlertDialog>
                        <AlertDialogTrigger asChild>
                            <button
                                type="button"
                                aria-label="Delete review"
                                title="Delete review"
                                disabled={isDeleting}
                                className={'inline-flex h-9 items-center gap-2 rounded-full bg-red-50 px-3 font-plus-jakarta-sans text-[14px] text-red-600 transition-colors hover:bg-red-100 disabled:cursor-not-allowed disabled:opacity-60'}
                            >
                                <Trash2 className={'size-5'} aria-hidden="true"/>
                                Delete
                            </button>
                        </AlertDialogTrigger>
                        <AlertDialogContent>
                            <AlertDialogHeader>
                                <AlertDialogTitle>Delete review?</AlertDialogTitle>
                                <AlertDialogDescription>
                                    This removes your review from this book.
                                </AlertDialogDescription>
                            </AlertDialogHeader>
                            <AlertDialogFooter>
                                <AlertDialogCancel disabled={isDeleting}>Cancel</AlertDialogCancel>
                                <AlertDialogAction
                                    disabled={isDeleting}
                                    onClick={handleDelete}
                                    className={'bg-red-600 text-white hover:bg-red-700'}
                                >
                                    {isDeleting ? 'Deleting...' : 'Delete'}
                                </AlertDialogAction>
                            </AlertDialogFooter>
                        </AlertDialogContent>
                    </AlertDialog>
                )}
            </div>
        </section>
    )
}
