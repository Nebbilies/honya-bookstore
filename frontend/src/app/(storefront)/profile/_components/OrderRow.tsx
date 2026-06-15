'use client';

import {useRouter} from "next/navigation";
import {Order} from "@/types/types";
import {OrderStatusBadge, providerLabel, formatVnd, formatDate, isRepayable} from "./order-format";
import RepayButton from "./RepayButton";

export default function OrderRow({order}: {order: Order}) {
    const router = useRouter();
    return (
        <tr
            onClick={() => router.push(`/profile/orders/${order.id}`)}
            className={'cursor-pointer border-t border-line-color transition-colors hover:bg-gray-50'}>
            <td className={'px-4 py-4'}>{formatDate(order.createdAt)}</td>
            <td className={'px-4 py-4'}>{`${order.lastName} ${order.firstName}`.trim()}</td>
            <td className={'px-4 py-4 font-medium'}>{formatVnd(order.totalAmount)}</td>
            <td className={'px-4 py-4'}><OrderStatusBadge status={order.status}/></td>
            <td className={'px-4 py-4 text-gray-600'}>{providerLabel(order.provider)}</td>
            <td className={'px-4 py-4 text-right'}>
                {isRepayable(order) && <RepayButton orderId={order.id}/>}
            </td>
        </tr>
    );
}
