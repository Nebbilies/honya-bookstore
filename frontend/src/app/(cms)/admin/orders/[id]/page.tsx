import {notFound} from "next/navigation";
import {Metadata} from "next";
import {auth} from "@/auth";
import {Order} from "@/types/types";
import OrderDetailsForm from "@/app/(cms)/admin/orders/_components/OrderDetailsForm";

export const metadata: Metadata = {
    title: 'Order Details',
    description: 'View order details and update its status',
};

async function getOrder(id: string): Promise<Order | null> {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    const session = await auth();
    const res = await fetch(`${apiBaseUrl}/orders/${id}`, {
        headers: {authorization: `Bearer ${session?.accessToken}`},
        cache: 'no-store',
    });
    if (!res.ok) return null;
    return res.json();
}

export default async function AdminOrderDetailsPage({params}: {params: Promise<{id: string}>}) {
    const {id} = await params;
    const order = await getOrder(id);
    if (!order) {
        notFound();
    }

    return (
        <main className={'flex flex-col gap-6'}>
            <h1 className={'font-prata text-3xl'}>Order Details</h1>
            <OrderDetailsForm order={order}/>
        </main>
    );
}
