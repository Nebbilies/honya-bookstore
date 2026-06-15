'use client'

import { useState } from "react";
import { useSession } from "next-auth/react";
import { toast } from "sonner";
import Icon from "@/components/Icon";
import { publicApiUrl } from "@/lib/api-url";

interface ReviewControlProps {
    reviewId: string;
    initialVoteCount: number;
    initialUserVote: 'upvote' | 'downvote' | null;
}

export default function ReviewControl({ reviewId, initialVoteCount, initialUserVote }: ReviewControlProps) {
    const session = useSession();
    const [voteCount, setVoteCount] = useState<number>(initialVoteCount);
    const [userVote, setUserVote] = useState<'upvote' | 'downvote' | null>(initialUserVote);
    const [isVoting, setIsVoting] = useState(false);

    const handleVote = async (value: 'UP' | 'DOWN') => {
        if (session.status !== 'authenticated' || !session.data?.accessToken) {
            toast.error('Please sign in to vote');
            return;
        }
        if (isVoting) {
            return;
        }
        setIsVoting(true);

        try {
            const res = await fetch(publicApiUrl(`/reviews/${reviewId}/vote`), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session.data.accessToken}`,
                },
                body: JSON.stringify({ value }),
            });

            if (!res.ok) {
                toast.error('Failed to register your vote');
                return;
            }

            const data: { voteCount: number; userVote: 'UP' | 'DOWN' | null } = await res.json();
            setVoteCount(data.voteCount);
            setUserVote(data.userVote === 'UP' ? 'upvote' : data.userVote === 'DOWN' ? 'downvote' : null);
        } catch {
            toast.error('Could not reach the review service');
        } finally {
            setIsVoting(false);
        }
    };

    const iconSize = 25;
    return (
        <div className={'flex items-center gap-0.5 bg-[#d3d0c2] w-fit rounded-full px-1'}>
            <button
                onClick={() => handleVote('UP')}
                disabled={isVoting}
                className={`p-1 cursor-pointer disabled:opacity-60 ${userVote === 'upvote' ? 'text-blue-400 font-bold' : 'text-gray-600'}
                hover:text-blue-500 transition-all duration-200`}
            >
                <Icon name={'upvote'} size={iconSize}/>
            </button>
            <span className={'text-gray-800 font-plus-jakarta-sans text-[15px]'}>{voteCount}</span>
            <button
                onClick={() => handleVote('DOWN')}
                disabled={isVoting}
                className={`p-1 cursor-pointer disabled:opacity-60 ${userVote === 'downvote' ? 'text-red-400 font-bold' : 'text-gray-600'}
                hover:text-red-500 transition-all duration-200`}
            >
                <Icon name={'downvote'} size={iconSize}/>
            </button>
        </div>
    )
}
