'use client'
import {useState} from "react";
import Link from "next/link";
import Button from "@/components/Button";
import {Icon} from "@iconify/react";
import {useOrder} from "@/app/(storefront)/_context/OrderContext";
import {useSession} from "next-auth/react";
import {toast} from "sonner";

export default function PaymentInformation() {
    const orderContext = useOrder();
    const session = useSession();

    const [paymentMethod, setPaymentMethod] = useState<string>('credit-card');

    const handleProceed = async () => {
        orderContext.setOrderInformation({
            ...orderContext.orderInformation!,
            provider: paymentMethod === 'credit-card' ? 'VNPAY' : 'COD',
        });

        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/orders`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                authorization: `Bearer ${session.data?.accessToken}`,
            },
            body: JSON.stringify({
                ...orderContext.orderInformation,
                provider: paymentMethod === 'credit-card' ? 'VNPAY' : 'COD',
            }),
        })

        if (!res.ok) {
            const errorData = await res.json();
            toast.error(errorData.message || 'Failed to create order');
            throw new Error(errorData.message || 'Failed to create order');
        }

        const data = await res.json();
        if (paymentMethod === 'credit-card') {
            window.location.href = `${data.paymentUrl}`;
        } else {
            toast.success('Order placed successfully.');
            window.location.href = '/checkout/payment/success?orderId=' + data.id;
        }
    }

    return (
        <section className={'w-full flex flex-col items-start gap-6 bg-white px-[15px] py-[25px] rounded-lg'}>
            <h4 className={'text-[26px] font-prata'}>Payment Information</h4>
            <label className={'text-[18px] font-plus-jakarta-sans'}>Payment Method</label>
            <div className={'flex flex-col gap-2 w-full -mt-2'}>
                <label className={'flex items-center gap-4'}>
                    <input
                        type={'radio'}
                        name={'payment-method'}
                        value={'credit-card'}
                        checked={paymentMethod === 'credit-card'}
                        onChange={() => setPaymentMethod('credit-card')}
                    />
                    <Icon icon={'mdi:credit-card-outline'} className={'w-6 h-6 inline-block -mr-2'}/>
                    Credit Card (VNPAY)
                </label>
                <label className={'flex items-center gap-4'}>
                    <input
                        type={'radio'}
                        name={'payment-method'}
                        value={'cash-on-delivery'}
                        checked={paymentMethod === 'cash-on-delivery'}
                        onChange={() => setPaymentMethod('cash-on-delivery')}
                    />
                    Cash on Delivery (COD)
                </label>
            </div>
            <div className={'w-full flex items-start justify-between gap-4 '}>
                <Link href={'/checkout'} className={'mt-4 w-full'}>
                    <Button
                        shape={'rect'}
                        variant={'outline'}
                        className={'text-black hover:bg-gray-100 font-plus-jakarta-sans font-light text-[18px] w-full py-4'}>
                        Back
                    </Button>
                </Link>
                <div className={'mt-4 w-full'}>
                    {paymentMethod === 'cash-on-delivery' ? (
                        <Button
                            shape={'rect'}
                            variant={'solid'}
                            onClick={handleProceed}
                            className={'bg-black text-white hover:bg-gray-700 font-plus-jakarta-sans font-light text-[18px] w-full py-4'}>
                            Proceed
                        </Button>
                        ) : (
                        <Button
                            shape={'rect'}
                            variant={'solid'}
                            icon={'popout'}
                            iconPosition={'left'}
                            onClick={handleProceed}
                            iconSize={25}
                            className={'bg-black text-white hover:bg-gray-700 font-plus-jakarta-sans font-light text-[18px] w-full py-4'}>
                            Proceed
                        </Button>
                    )}
                </div>
            </div>
        </section>
    )
}