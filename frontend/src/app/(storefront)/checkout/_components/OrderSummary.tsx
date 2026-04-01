'use client';
import {useCart} from "@/app/(storefront)/_context/CartContext";

export default function OrderSummary() {
    const {cartItems, subtotal} = useCart();
    return (
        <div
            className={'flex flex-col items-center justify-center p-[10px] gap-6 bg-white h-fit rounded-lg w-fit border-1'}>
            <span className={'text-[24px] font-prata'}>Order Summary</span>
            <div className={'flex flex-col font-plus-jakarta-sans text-[20px] min-w-[300px] px-[10px] gap-3 -mt-2'}>
                {cartItems.length > 0 && (
                    cartItems.map((item, index) => (
                        <div key={index} className={'flex-col flex'}>
                            <div className={'flex justify-between gap-10 font-prata text-[14px] text-black'}>
                                <span className={'line-clamp-1'}>{item.book.title}</span>
                                <span className={'line-clamp-1'}>{(item.book.price * item.quantity).toLocaleString("vi-VN", {
                                    style: "currency",
                                    currency: "VND",
                                })}</span>
                            </div>
                            <span className={'font-plus-jakarta-sans text-[12px] text-gray-500'}>
                                Qty: {item.quantity}
                            </span>
                        </div>
                    ))
                )}
                <div className={'border-1 border-line-color w-full'}/>
                <div className={'flex justify-between'}>
                    <span>Subtotal</span>
                    <span>{subtotal.toLocaleString("vi-VN", {
                        style: "currency",
                        currency: "VND",
                    })}
                    </span>
                </div>
                <div className={'flex justify-between'}>
                    <span>Shipping</span>
                    <span>Free</span>
                </div>
                <div className={'flex justify-between'}>
                    <span>Tax</span>
                    <span>{(subtotal * 0.1).toLocaleString("vi-VN", {
                        style: "currency",
                        currency: "VND",
                    }
                    )}
                    </span>
                </div>
                <div className={'border-1 border-line-color w-full'}/>
                <div className={'flex justify-between font-prata text-[22px] tracking-wide'}>
                    <span>Total</span>
                    <span>{(subtotal * 1.1).toLocaleString("vi-VN", {
                        style: "currency",
                        currency: "VND",
                    }
                    )}</span>
                </div>
            </div>
        </div>
    )
}