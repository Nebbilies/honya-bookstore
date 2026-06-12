import {Order} from "@/types/types";

const STATUS_STYLES: Record<Order['status'], string> = {
    PENDING: 'bg-amber-100 text-amber-700',
    PROCESSING: 'bg-blue-100 text-blue-700',
    SHIPPING: 'bg-indigo-100 text-indigo-700',
    DELIVERED: 'bg-green-100 text-green-700',
    CANCELLED: 'bg-red-100 text-red-700',
};

const PROVIDER_LABELS: Record<Order['provider'], string> = {
    COD: 'Cash on Delivery',
    VNPAY: 'VNPay',
    MOMO: 'MoMo',
    ZALOPAY: 'ZaloPay',
};

export function OrderStatusBadge({status}: {status: Order['status']}) {
    return (
        <span className={`inline-block rounded-full px-3 py-1 text-xs font-medium ${STATUS_STYLES[status] ?? 'bg-gray-100 text-gray-700'}`}>
            {status.charAt(0) + status.slice(1).toLowerCase()}
        </span>
    );
}

export function providerLabel(provider: Order['provider']) {
    return PROVIDER_LABELS[provider] ?? provider;
}

export function formatVnd(amount: number) {
    return amount.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'});
}

export function formatDate(value: string) {
    return new Date(value).toLocaleDateString('en-GB', {day: '2-digit', month: '2-digit', year: 'numeric'});
}
