'use client';

import {useState} from "react";
import {useRouter} from "next/navigation";
import {useSession} from "next-auth/react";
import {toast} from "sonner";
import Icon from "@/components/Icon";
import {Order} from "@/types/types";

const STATUS_OPTIONS = ['PENDING', 'PROCESSING', 'SHIPPING', 'DELIVERED', 'CANCELLED'];

function ReadonlyField({label, value, className}: {label: string, value: string, className?: string}) {
    return (
        <div className={`flex flex-col gap-1.5 ${className ?? ''}`}>
            <label className={'text-[15px] text-black'}>{label}</label>
            <div className={'rounded-lg bg-disabled-color px-4 py-3 text-gray-500'}>{value || '—'}</div>
        </div>
    );
}

export default function OrderDetailsForm({order}: {order: Order}) {
    const router = useRouter();
    const session = useSession();
    const [status, setStatus] = useState<Order['status']>(order.status);
    const [saving, setSaving] = useState(false);

    const recipient = `${order.lastName ?? ''} ${order.firstName ?? ''}`.trim();
    const address = `${order.address ?? ''}${order.city ? `, ${order.city}` : ''}`;
    const price = order.totalAmount.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'});
    const payment = order.provider === 'COD' ? 'Cash' : 'Credit Card';
    const changed = status !== order.status;

    const handleSave = async () => {
        if (!changed) return;
        setSaving(true);
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/orders/${order.id}/status`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session.data?.accessToken}`,
                },
                body: JSON.stringify({status}),
            });
            if (!res.ok) {
                const err = await res.json();
                toast.error(err.message || 'Failed to update order status');
                return;
            }
            toast.success('Order status updated');
            router.refresh();
        } catch (error) {
            console.error(error);
            toast.error('Failed to update order status');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className={'flex flex-col gap-6'}>
            <div className={'rounded-2xl border border-line-color p-6'}>
                <div className={'grid grid-cols-1 gap-6 md:grid-cols-2'}>
                    <div className={'flex flex-col gap-6'}>
                        <ReadonlyField label={'Recipient'} value={recipient}/>
                        <div className={'flex flex-col gap-1.5'}>
                            <label className={'text-[15px] text-black'}>Address</label>
                            <div className={'min-h-[180px] rounded-lg bg-disabled-color px-4 py-3 text-gray-500'}>{address || '—'}</div>
                        </div>
                    </div>

                    <div className={'flex flex-col gap-6'}>
                        <ReadonlyField label={'Recipient Phone Number'} value={order.phone}/>
                        <ReadonlyField label={'Price'} value={price}/>
                        <div className={'grid grid-cols-2 gap-4'}>
                            <ReadonlyField label={'Payment Method'} value={payment}/>
                            <div className={'flex flex-col gap-1.5'}>
                                <label className={'text-[15px] text-black'}>Order Status</label>
                                <select
                                    value={status}
                                    onChange={(e) => setStatus(e.target.value as Order['status'])}
                                    className={'cursor-pointer rounded-lg border border-line-color bg-white px-4 py-3'}>
                                    {STATUS_OPTIONS.map((option) => (
                                        <option key={option} value={option}>
                                            {option.charAt(0) + option.slice(1).toLowerCase()}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <button
                onClick={handleSave}
                disabled={!changed || saving}
                className={'inline-flex w-fit items-center gap-2 rounded-xl bg-blue-500 px-6 py-3 text-white transition-colors hover:bg-blue-600 disabled:cursor-not-allowed disabled:opacity-50'}>
                <Icon name={'confirm'} size={22}/>
                {saving ? 'Saving...' : 'Save Changes'}
            </button>
        </div>
    );
}
