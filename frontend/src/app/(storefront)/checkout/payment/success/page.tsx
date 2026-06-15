import {Check, TriangleAlert} from "lucide-react";
import Link from "next/link";
import {Order} from "@/types/types";
import { auth } from "@/auth";
import {Metadata} from "next";
import RepayButton from "@/app/(storefront)/profile/_components/RepayButton";

export const dynamic = 'force-dynamic';

export const metadata: Metadata = {
    title: 'Payment Result',
    description: 'The result of your order payment.',
}

interface PaymentSuccessPageProps {
    searchParams: { [key: string]: string | string[] | undefined };
}

export default async function PaymentSuccessPage(
    { searchParams }: PaymentSuccessPageProps
) {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    const session = await auth();
    const params = await searchParams;
    const { vnp_TxnRef, orderId, vnp_ResponseCode } = params;

    const orderIdFromParam = vnp_TxnRef ? vnp_TxnRef.toString() : orderId ? orderId.toString() : null;
    const responseCode = vnp_ResponseCode ? vnp_ResponseCode.toString() : null;
    const failed = responseCode !== null && responseCode !== '00';

    if (!orderIdFromParam) {
        return (
            <section className={'max-w-7xl min-h-[60vh] flex flex-col items-center justify-center gap-6'}>
                <h2 className={'text-[32px] font-prata'}>Payment Failed or Invalid Request!</h2>
            </section>
        )
    }

    let order: Order | null = null;
    const res = await fetch(`${apiBaseUrl}/orders/me/${orderIdFromParam}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            authorization: `Bearer ${session?.accessToken}`,
        },
        cache: 'no-store',
    });
    if (res.ok) {
        order = await res.json();
    }

    if (failed) {
        return (
            <section className={'max-w-7xl min-h-[60vh] flex flex-col items-center justify-center gap-6 mb-6'}>
                <TriangleAlert className={'size-[160px] text-amber-500'}/>
                <h2 className={'text-[32px] font-prata'}>Payment Unsuccessful</h2>
                <p className={'text-[18px] text-gray-600'}>Your payment was not completed. Your order is still pending.</p>
                <div className={'flex items-center gap-4'}>
                    {order && <RepayButton orderId={order.id}/>}
                    <Link href={'/profile'} className={'text-[16px] underline'}>View my orders</Link>
                </div>
            </section>
        )
    }

    return (
        <section className={'max-w-7xl min-h-[60vh] flex flex-col items-center justify-center gap-6 mb-6'}>
            <Check className={'size-[400px]'}/>
            <h2 className={'text-[32px] font-prata'}>Payment Successful!</h2>
            <p className={'text-[18px]'}>Your order ID is <strong>{order?.id ?? orderIdFromParam}</strong></p>
            {order && <p className={'text-[18px]'}>Order total price: <strong>{order.totalAmount}</strong></p>}
        </section>
    )
}
