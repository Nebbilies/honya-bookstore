import {Review} from "@/types/types";
import ReviewCard from "@/app/(storefront)/books/_components/Review/ReviewCard";
import review from "@/mocks/data/review.json";

interface ReviewListProps {
    bookId: string;
}

// TODO: implement pagination, change fetch url
export default async function ReviewList({ bookId }: ReviewListProps) {
    const reviews = review
    return (
        <section className={'w-full flex flex-col gap-8 items-center mx-auto'}>
            {reviews.map((review: Review) => (
                <ReviewCard key={review.id} review={review}/>
            ))}
        </section>
    )
}
