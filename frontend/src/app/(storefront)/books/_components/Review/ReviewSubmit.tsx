'use client';
import { useState } from "react";
import SectionTitle from "@/app/(storefront)/_components/SectionTitle";
import StarIcon from "@/components/Icon/StarIcon";
import LongField from "@/components/Input Field/LongField";
import Button from "@/components/Button";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

interface ReviewSubmitProps {
    bookId: string;
}

export default function ReviewSubmit({bookId}: ReviewSubmitProps) {
    const router = useRouter();
    const session = useSession();
    const [selectedRating, setSelectedRating] = useState<number | null>(null);
    const [hoveredRating, setHoveredRating] = useState<number | null>(null);
    const [reviewText, setReviewText] = useState<string>('');
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async () => {
        setError(null);

        if (session.status !== 'authenticated' || !session.data?.accessToken) {
            setError('Please sign in to submit a review');
            toast.error('Please sign in to submit a review');
            return;
        }

        if (selectedRating === null) {
            setError('Please choose a rating');
            toast.error('Please choose a rating');
            return;
        }

        if (!reviewText.trim()) {
            setError('Please write a review');
            toast.error('Please write a review');
            return;
        }

        let res: Response;
        try {
            res = await fetch('/api/reviews', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    bookId,
                    rating: selectedRating + 1,
                    content: reviewText.trim(),
                }),
            });
        } catch (error) {
            console.error('Failed to submit review:', error);
            setError('Could not reach the review service');
            toast.error('Could not reach the review service');
            return;
        }

        if (!res.ok) {
            let message = 'Failed to submit review';
            try {
                const data = await res.json();
                message = data.message || data.detail || data.title || message;
            } catch {
                // Keep the generic message when the backend returns a non-JSON error.
            }
            setError(message);
            toast.error(message);
            return;
        }

        setSelectedRating(null);
        setReviewText('');
        toast.success('Review submitted');
        router.refresh();
    };

    return (
        <section className={'w-full flex flex-col items-center gap-10 mx-auto'}>
            <SectionTitle title={'Write a Review'}/>
            <div className={'flex flex-col gap-10 w-full items-center'}>
                <div className={'flex items-center'}>
                    {Array.from({ length: 5 }).map((_, index) => {
                        const starEmptyStyle = 'stroke-[#757575] fill-none';
                        const starFullStyle = 'stroke-[#757575] fill-[#fff7a1]';
                        return (
                            <div key={index}
                                 onClick={() => setSelectedRating(index)}
                                 onMouseEnter={() => setHoveredRating(index)}
                                 onMouseLeave={() => setHoveredRating(null)}
                            >
                                <StarIcon className={`w-[100px] h-[100px] cursor-pointer ${hoveredRating !== null ? (index <= hoveredRating ? starFullStyle : starEmptyStyle) :
                                    (selectedRating !== null && index <= selectedRating ? starFullStyle : starEmptyStyle)}`}/>
                            </div>
                        )
                    })}
                </div>
                <div className={'w-3/5 flex justify-center'}>
                    <LongField onValueChange={(e) => setReviewText(e.target.value)}
                               value={reviewText}
                               placeholder={'Write your review here...'}
                               rows={6}
                               charLimit={300}
                               className={'p-4'}/>
                </div>
                <Button variant={"solid"} shape={'rect'} width={280} height={60}
                        type="button"
                        onClick={handleSubmit}
                        className={'font-plus-jakarta-sans text-[20px] rounded-[20px]'}>
                    Submit Review
                </Button>
                {error && (
                    <p className={'font-plus-jakarta-sans text-[14px] text-red-600 text-center'}>
                        {error}
                    </p>
                )}
            </div>
            <div className={'w-4/5 h-[2px] bg-line-color'}/>
        </section>
    )
}
