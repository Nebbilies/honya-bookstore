import {OrderResponse} from "@/types/types";
import {Metadata} from "next";
import {CustomPagination} from "@/components/Pagination/CustomPagination";
import {auth} from "@/auth";

export const metadata: Metadata = {
    title: 'Order List',
    description: 'Manage the list of orders in the bookstore',
}

async function getOrders(page: number): Promise<OrderResponse> {
    const session = await auth();
    const params = new URLSearchParams();
    params.set('page', page.toString());
    params.set('limit', '10');

    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/orders?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            authorization: `Bearer ${session?.accessToken}`,
        },
        cache: 'no-store'
    });

    if (!res.ok) {
        const errorData = await res.json();
        console.error('Failed to fetch orders:', errorData.message || 'Unknown error');
        return {
            data: [],
            meta: { totalItems: 0, pageItems: 0, itemsPerPage: 10, totalPages: 0, currentPage: 1 }
        };
    }

    return res.json();
}

export default async function BookListPage({searchParams}: { searchParams: { search?: string, page?: number } }) {
    const params = await searchParams;
    const page = params?.page || 1;
    const ordersData: OrderResponse = await getOrders(page);
    const orders = ordersData.data;
    return (
        <main className={'flex flex-col gap-6'}>
            <div className={'flex justify-between items-center'}>
                <h1 className={'font-prata text-3xl'}>Order List</h1>
            </div>
            <table className="w-full border-collapse rounded-lg overflow-hidden shadow-sm">
                <thead>
                <tr className="bg-gray-100 text-left text-[16px] font-bold text-gray-700">
                    <th className="px-4 py-3 border-r border-gray-200">Recipient Name</th>
                    <th className="px-4 py-3 border-r border-gray-200">Phone Num.</th>
                    <th className="px-4 py-3 border-r border-gray-200">Address</th>
                    <th className="px-4 py-3 border-r border-gray-200 text-center">Payment Method</th>
                    <th className="px-4 py-3 border-r border-gray-200 text-center">Order Status</th>
                    <th className="px-4 py-3 border-r border-gray-200">Total</th>
                    <th className="px-4 py-3 border-r border-gray-200">Date</th>
                    <th className="px-4 py-3 text-center">Actions</th>
                </tr>
                </thead>

                <tbody className="divide-y divide-gray-100 text-sm text-gray-700">
                {orders.map((ord, index) => (
                    <tr
                        key={index}
                        className="hover:bg-gray-50 transition-colors duration-200"
                    >
                        <td className="px-4 py-3 border-gray-100">
                            {ord.lastName + ' ' + ord.firstName}
                        </td>
                        <td className="px-4 py-3 border-gray-100 font-medium">{ord.phone}</td>
                        <td className="px-4 py-3 border-gray-100 line-clamp-3">{ord.address}</td>
                        <td className="px-4 py-3 border-gray-100 text-center">{ord.provider === "COD" ? "Cash" : "Credit Card"}</td>
                        <td className="px-4 py-3 border-gray-100 text-center">{ord.status}</td>
                        <td className="px-4 py-3 border-gray-100">{ord.totalAmount.toLocaleString("vi-VN", {
                            style: "currency",
                            currency: "VND",
                        })}</td>
                        <td className="px-4 py-3 border-gray-100">{new Date(ord.createdAt).toLocaleString()}</td>
                        <td className="px-4 py-3 border-gray-100 text-center">Placeholder</td>
                    </tr>
                ))}
                </tbody>
            </table>
            <div className={'self-end'}>
                <CustomPagination totalPages={ordersData.meta.totalPages}/>
            </div>
        </main>
    )
}