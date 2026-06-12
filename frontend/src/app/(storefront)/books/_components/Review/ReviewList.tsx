import {Review, ReviewResponse} from "@/types/types";
import ReviewCard from "@/app/(storefront)/books/_components/Review/ReviewCard";
import { serverApiUrl } from "@/lib/api-url";
import { auth } from "@/auth";

interface ReviewListProps {
    bookId: string;
}

export default async function ReviewList({ bookId }: ReviewListProps) {
    const session = await auth();
    const currentUserId = session?.user?.id ?? null;
    const params = new URLSearchParams({
        bookId,
        page: '1',
        limit: '10',
    });

    const res = await fetch(serverApiUrl(`/reviews?${params.toString()}`), {cache: 'no-store'});
    if (!res.ok) {
        return null;
    }

    const reviewsData: ReviewResponse = await res.json();
    const reviews = reviewsData.data;

    return (
        <section className={'w-full flex flex-col gap-8 items-center mx-auto'}>
            {reviews.map((review: Review) => (
                <ReviewCard
                    key={review.id}
                    review={review}
                    canDelete={currentUserId === review.userId}
                />
            ))}
        </section>
    )
}
