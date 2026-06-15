'use client';

import {useSession} from "next-auth/react";
import {toast} from "sonner";
import Button from "@/components/Button";

export default function RepayButton({orderId, className}: {orderId: string; className?: string}) {
    const session = useSession();

    const handleRepay = async () => {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/orders/me/${orderId}/repay`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                authorization: `Bearer ${session.data?.accessToken}`,
            },
        });

        if (!res.ok) {
            const errorData = await res.json().catch(() => ({}));
            toast.error(errorData.detail || errorData.message || 'Unable to start payment');
            return;
        }

        const data = await res.json();
        if (data.paymentUrl) {
            window.location.href = data.paymentUrl;
        } else {
            toast.error('Payment link unavailable');
        }
    };

    return (
        <span onClick={(event) => event.stopPropagation()} className={'inline-block'}>
            <Button
                shape={'rect'}
                variant={'solid'}
                onClick={handleRepay}
                className={`bg-black text-white hover:bg-gray-700 font-plus-jakarta-sans text-sm px-4 py-2 ${className ?? ''}`}>
                Pay now
            </Button>
        </span>
    );
}
