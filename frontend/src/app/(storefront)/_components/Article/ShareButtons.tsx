'use client';

import Icon from "@/components/Icon";
import { toast } from "sonner";

export default function ShareButtons({ title }: { title: string }) {
    const openShare = (platform: 'facebook' | 'twitter') => {
        const url = encodeURIComponent(window.location.href);
        const text = encodeURIComponent(title);
        const links: Record<string, string> = {
            facebook: `https://www.facebook.com/sharer/sharer.php?u=${url}`,
            twitter: `https://twitter.com/intent/tweet?url=${url}&text=${text}`,
        };
        window.open(links[platform], '_blank', 'noopener,noreferrer');
    };

    const copyLink = async () => {
        try {
            await navigator.clipboard.writeText(window.location.href);
            toast.success('Link copied to clipboard');
        } catch {
            toast.error('Could not copy link');
        }
    };

    return (
        <div className={'flex flex-col items-end gap-3'}>
            <div className={'flex items-center gap-2 text-black'}>
                <Icon name={'share'} size={20}/>
                <span className={'font-plus-jakarta-sans text-[13px]'}>Share This Article</span>
            </div>
            <div className={'flex items-center gap-4'}>
                <button
                    type={'button'}
                    onClick={() => openShare('facebook')}
                    aria-label={'Share on Facebook'}
                    className={'cursor-pointer hover:opacity-60 transition-opacity duration-200'}
                >
                    <Icon name={'facebook'} size={20}/>
                </button>
                <button
                    type={'button'}
                    onClick={() => openShare('twitter')}
                    aria-label={'Share on X'}
                    className={'cursor-pointer hover:opacity-60 transition-opacity duration-200'}
                >
                    <Icon name={'twitter'} size={20}/>
                </button>
                <button
                    type={'button'}
                    onClick={copyLink}
                    aria-label={'Copy link'}
                    className={'cursor-pointer hover:opacity-60 transition-opacity duration-200'}
                >
                    <Icon name={'instagram'} size={20}/>
                </button>
            </div>
        </div>
    );
}
