'use client'
import {useCart} from "@/app/(storefront)/_context/CartContext";
import Icon from "@/components/Icon";
import OrderSummary from "@/app/(storefront)/checkout/_components/OrderSummary";
import Link from "next/link";

export default function LayoutClient({ children } : { children: React.ReactNode }) {
    const cartContext = useCart();
    
    return (
        <main className={'flex flex-col items-center justify-start gap-8 py-20 w-2/3'}>
            {cartContext.cartItems.length === 0 ? (
                <div className={'flex flex-col items-center justify-center gap-6'}>
                    <span className={'font-prata text-[28px] w-full text-center'}>
                        Your cart is empty
                    </span>
                    <span className={'font-plus-jakarta-sans text-[18px] text-gray-600 text-center'}>
                        Looks like you haven&#39;t added anything to your <Link className={'text-blue-600 underline'} href={'/cart'}>cart</Link> yet.
                    </span>
                </div>
            ) : (
                <>
                    <header className={'flex flex-col gap-4 w-full'}>
                <span className={'font-prata text-[28px] w-full'}>
                Checkout
            </span>
                        <div className={'flex w-full items-center font-prata text-[20px] gap-2'}>
                            <div
                                className={'text-center flex items-center justify-center aspect-square rounded-full bg-black text-white w-10 align-middle'}>
                                1
                            </div>
                            Shipping
                            <Icon name={"right-arrow"} className={'h-6 w-6'}/>
                            <div
                                className={'text-center flex items-center justify-center aspect-square rounded-full bg-black text-white w-10 align-middle'}>
                                2
                            </div>
                            Payment
                        </div>
                    </header>
                    <div className={'flex items-start justify-between w-full gap-10'}>
                        {children}
                        <OrderSummary/>
                    </div>
                </>
            )}
        </main>
    )
}