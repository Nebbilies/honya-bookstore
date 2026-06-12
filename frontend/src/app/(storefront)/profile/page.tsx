import {redirect} from "next/navigation";
import {auth} from "@/auth";
import {OrderResponse} from "@/types/types";
import {CustomPagination} from "@/components/Pagination/CustomPagination";
import OrderRow from "@/app/(storefront)/profile/_components/OrderRow";

export const dynamic = 'force-dynamic';

async function getMyOrders(accessToken: string, page: number): Promise<OrderResponse> {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    const res = await fetch(`${apiBaseUrl}/orders/me?page=${page}&limit=10`, {
        headers: {authorization: `Bearer ${accessToken}`},
        cache: 'no-store',
    });
    if (!res.ok) {
        return {data: [], meta: {totalItems: 0, pageItems: 0, itemsPerPage: 10, totalPages: 0, currentPage: 1}};
    }
    return res.json();
}

export default async function ProfileOrdersPage({searchParams}: {searchParams: Promise<{page?: string}>}) {
    const session = await auth();
    if (!session?.accessToken) {
        redirect('/api/auth/signin?callbackUrl=/profile');
    }

    const params = await searchParams;
    const page = Number(params?.page) || 1;
    const data = await getMyOrders(session.accessToken, page);
    const orders = data.data;

    return (
        <section className={'rounded-2xl border border-line-color bg-white p-6 shadow-sm'}>
            <h2 className={'font-prata text-2xl'}>My Orders</h2>
            <div className={'mt-4 border-t border-line-color'}/>

            {orders.length === 0 ? (
                <div className={'py-16 text-center text-gray-400'}>
                    You haven&apos;t placed any orders yet.
                </div>
            ) : (
                <div className={'mt-4 overflow-x-auto'}>
                    <table className={'w-full text-left text-sm text-gray-700'}>
                        <thead>
                        <tr className={'text-[15px] font-semibold text-gray-900'}>
                            <th className={'px-4 py-3'}>Date</th>
                            <th className={'px-4 py-3'}>Recipient</th>
                            <th className={'px-4 py-3'}>Total</th>
                            <th className={'px-4 py-3'}>Status</th>
                            <th className={'px-4 py-3'}>Payment</th>
                        </tr>
                        </thead>
                        <tbody>
                        {orders.map((order) => (
                            <OrderRow key={order.id} order={order}/>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {data.meta.totalPages > 1 && (
                <div className={'mt-6 flex justify-end'}>
                    <CustomPagination totalPages={data.meta.totalPages}/>
                </div>
            )}
        </section>
    );
}
