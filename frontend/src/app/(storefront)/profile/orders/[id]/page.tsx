import {notFound, redirect} from "next/navigation";
import Link from "next/link";
import {auth} from "@/auth";
import {Order} from "@/types/types";
import Icon from "@/components/Icon";
import {OrderStatusBadge, providerLabel, formatVnd, formatDate} from "@/app/(storefront)/profile/_components/order-format";

export const dynamic = 'force-dynamic';

async function getMyOrder(accessToken: string, id: string): Promise<Order | null> {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    const res = await fetch(`${apiBaseUrl}/orders/me/${id}`, {
        headers: {authorization: `Bearer ${accessToken}`},
        cache: 'no-store',
    });
    if (res.status === 404) return null;
    if (!res.ok) return null;
    return res.json();
}

export default async function OrderDetailPage({params}: {params: Promise<{id: string}>}) {
    const session = await auth();
    if (!session?.accessToken) {
        redirect('/api/auth/signin?callbackUrl=/profile');
    }

    const {id} = await params;
    const order = await getMyOrder(session.accessToken, id);
    if (!order) {
        notFound();
    }

    return (
        <section className={'rounded-2xl border border-line-color bg-white p-6 shadow-sm'}>
            <Link href={'/profile'} className={'inline-flex items-center gap-1 text-sm text-gray-500 hover:text-black'}>
                <Icon name={'right-arrow'} size={16} className={'rotate-180'}/>
                Back to orders
            </Link>

            <div className={'mt-4 flex flex-wrap items-center justify-between gap-3'}>
                <div>
                    <h2 className={'font-prata text-2xl'}>Order details</h2>
                    <p className={'mt-1 text-sm text-gray-500'}>
                        Placed on {formatDate(order.createdAt)}
                    </p>
                </div>
                <OrderStatusBadge status={order.status}/>
            </div>

            <div className={'mt-6 border-t border-line-color'}/>

            <div className={'mt-6 divide-y divide-line-color'}>
                {order.items?.map((item) => (
                    <div key={item.id} className={'flex items-start justify-between gap-4 py-4'}>
                        <div>
                            <p className={'font-medium text-gray-900'}>{item.book.title}</p>
                            <p className={'text-sm text-gray-500'}>{item.book.author}</p>
                            <p className={'mt-1 text-sm text-gray-500'}>
                                {formatVnd(item.price)} &times; {item.quantity}
                            </p>
                        </div>
                        <p className={'whitespace-nowrap font-medium'}>{formatVnd(item.price * item.quantity)}</p>
                    </div>
                ))}
            </div>

            <div className={'mt-4 flex items-center justify-between border-t border-line-color pt-4'}>
                <span className={'font-prata text-lg'}>Total</span>
                <span className={'font-prata text-lg'}>{formatVnd(order.totalAmount)}</span>
            </div>

            <div className={'mt-8 grid gap-6 sm:grid-cols-2'}>
                <div>
                    <h3 className={'text-sm font-semibold uppercase tracking-wide text-gray-500'}>Shipping</h3>
                    <p className={'mt-2 text-gray-900'}>{`${order.lastName} ${order.firstName}`.trim()}</p>
                    <p className={'text-gray-600'}>{order.address}{order.city ? `, ${order.city}` : ''}</p>
                    <p className={'text-gray-600'}>{order.phone}</p>
                    <p className={'text-gray-600'}>{order.email}</p>
                </div>
                <div>
                    <h3 className={'text-sm font-semibold uppercase tracking-wide text-gray-500'}>Payment</h3>
                    <p className={'mt-2 text-gray-900'}>{providerLabel(order.provider)}</p>
                    <p className={'text-gray-600'}>{order.isPaid ? 'Paid' : 'Not paid'}</p>
                </div>
            </div>
        </section>
    );
}
