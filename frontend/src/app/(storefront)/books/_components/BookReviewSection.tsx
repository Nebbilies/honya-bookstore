import ReviewSubmit from "@/app/(storefront)/books/_components/Review/ReviewSubmit";
import ReviewList from "@/app/(storefront)/books/_components/Review/ReviewList";

interface BookReviewSectionProps {
    bookId: string;
}

export default function BookReviewSection({bookId}: BookReviewSectionProps) {
    return (
        <section className={'w-4/5 flex flex-col items-center gap-10 mx-auto'}>
            <ReviewSubmit bookId={bookId}/>
            <ReviewList bookId={bookId}/>
        </section>
    )
}