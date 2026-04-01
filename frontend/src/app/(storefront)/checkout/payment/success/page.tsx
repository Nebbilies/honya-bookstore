import {Check} from "lucide-react";
import {Order} from "@/types/types";
import { auth } from "@/auth";
import {Metadata} from "next";

export const dynamic = 'force-dynamic';

export const metadata: Metadata = {
    title: 'Payment Success',
    description: 'Your payment was successful. Thank you for your purchase!',
}

interface PaymentSuccessPageProps {
    searchParams: { [key: string]: string | string[] | undefined };
}

export default async function PaymentSuccessPage(
    { searchParams }: PaymentSuccessPageProps
) {
    const session = await auth();
    const params = await searchParams;
    const { vnp_TxnRef, orderId } = params;

    let order: Order | null = null;
    const orderIdFromParam = vnp_TxnRef ? vnp_TxnRef.toString() : orderId ? orderId.toString() : null;

    if (!orderIdFromParam) {
        return (
            <section className={'max-w-7xl min-h-[60vh] flex flex-col items-center justify-center gap-6'}>
                <h2 className={'text-[32px] font-prata'}>Payment Failed or Invalid Request!</h2>
            </section>
        )
    } else {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/orders/${orderIdFromParam}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                authorization: `Bearer ${session?.accessToken}`,
            },
            cache: 'no-store',
        });

        if (!res.ok) {
            const errorData = await res.json();
            throw new Error(errorData.message || 'Failed to fetch order details');
        }

        order = await res.json();
    }

    return (
        <section className={'max-w-7xl min-h-[60vh] flex flex-col items-center justify-center gap-6 mb-6'}>
            {orderIdFromParam ? (
                <>
                    <Check className={'size-[400px]'}/>
                    <h2 className={'text-[32px] font-prata'}>Payment Successful!</h2>
                    <p className={'text-[18px]'}>Your order ID is <strong>{order?.id}</strong></p>
                    <p className={'text-[18px]'}>Order total price: <strong>{order?.totalAmount}</strong></p>
                </>
            ) : (
                <section className={'max-w-7xl min-h-[60vh] flex flex-col items-center justify-center gap-6'}>
                    <h2 className={'text-[32px] font-prata'}>Payment Failed or Invalid Request!</h2>
                </section>
            )}
        </section>
    )
}