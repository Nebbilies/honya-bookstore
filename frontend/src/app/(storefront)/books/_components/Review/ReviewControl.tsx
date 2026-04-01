'use client'

import { useState } from "react";
import Icon from "@/components/Icon";

//TODO: fetch review vote, implement api calls for add vote, remove vote, and change vote

interface ReviewControlProps {
    reviewId: string;
    initialVoteCount: number;
    initialUserVote: 'upvote' | 'downvote' | null;
}

export default function ReviewControl({ reviewId, initialVoteCount, initialUserVote }: ReviewControlProps) {
    const [voteCount, setVoteCount] = useState<number>(initialVoteCount);
    const [userVote, setUserVote] = useState<'upvote' | 'downvote' | null>(initialUserVote);

    const handleUpvote = () => {
        if (userVote === 'upvote') {
            setVoteCount(voteCount - 1);
            setUserVote(null);
        } else if (userVote === 'downvote') {
            setVoteCount(voteCount + 2);
            setUserVote('upvote');
        } else {
            setVoteCount(voteCount + 1);
            setUserVote('upvote');
        }
    }

    const handleDownvote = () => {
        if (userVote === 'downvote') {
            setVoteCount(voteCount + 1);
            setUserVote(null);
        } else if (userVote === 'upvote') {
            setVoteCount(voteCount - 2);
            setUserVote('downvote');
        } else {
            setVoteCount(voteCount - 1);
            setUserVote('downvote');
        }
    }

    const iconSize = 25;
    return (
        <div className={'flex items-center gap-0.5 bg-[#d3d0c2] w-fit rounded-full px-1'}>
            <button
                onClick={handleUpvote}
                className={`p-1 cursor-pointer ${userVote === 'upvote' ? 'text-blue-400 font-bold' : 'text-gray-600'}
                hover:text-blue-500 transition-all duration-200`}
            >
                <Icon name={'upvote'} size={iconSize}/>
            </button>
            <span className={'text-gray-800 font-plus-jakarta-sans text-[15px]'}>{voteCount}</span>
            <button
                onClick={handleDownvote}
                className={`p-1 cursor-pointer ${userVote === 'downvote' ? 'text-red-400 font-bold' : 'text-gray-600'}
                hover:text-red-500 transition-all duration-200`}
            >
                <Icon name={'downvote'} size={iconSize}/>
            </button>
        </div>
    )
}