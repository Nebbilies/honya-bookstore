'use client';
import { useState, useEffect } from "react";
import SectionTitle from "@/app/(storefront)/_components/SectionTitle";
import Icon from "@/components/Icon";
import StarIcon from "@/components/Icon/StarIcon";
import LongField from "@/components/Input Field/LongField";
import Button from "@/components/Button";
import ReviewList from "@/app/(storefront)/books/_components/Review/ReviewList";

interface ReviewSubmitProps {
    bookId: string;
}

export default function ReviewSubmit({bookId}: ReviewSubmitProps) {
    const [selectedRating, setSelectedRating] = useState<number | null>(null);
    const [hoveredRating, setHoveredRating] = useState<number | null>(null);
    const [reviewText, setReviewText] = useState<string>('');
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
                        className={'font-plus-jakarta-sans text-[20px] rounded-[20px]'}>
                    Submit Review
                </Button>
            </div>
            <div className={'w-4/5 h-[2px] bg-line-color'}/>
        </section>
    )
}