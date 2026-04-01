import {Review} from "@/types/types";
import StarIcon from "@/components/Icon/StarIcon";
import ReviewControl from "@/app/(storefront)/books/_components/Review/ReviewControl";

interface ReviewCardProps {
    review: Review;
}

// TODO: Complete review user related stuff based on data from backend, fetch user vote data, currently using placeholder data
export default function ReviewCard({ review }: ReviewCardProps) {
    return (
        <section className={'w-full max-w-[1000px] bg-[#edebe2] rounded-[20px] p-[20px] flex flex-col gap-4'}>
            <div className={'flex gap-4 items-center'}>
                <div className={'w-[60px] h-[60px] rounded-full bg-cover bg-center'}
                        style={{backgroundImage: `url('/images/avatarPlaceholder.png')`}}/>
                <span className={'font-prata text-[22px]'}>astra_yao</span>
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
            <ReviewControl reviewId={review.id} initialVoteCount={4} initialUserVote={null}/>
        </section>
    )
}