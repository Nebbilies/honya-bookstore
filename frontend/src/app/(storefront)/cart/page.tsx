'use client';

import {useCart} from "@/app/(storefront)/_context/CartContext";
import {CartItem as CartItemType} from "@/types/types";
import CartItem from "@/app/(storefront)/cart/_components/CartItem";
import Button from "@/components/Button";
import Link from "next/link";

export const dynamic = 'force-dynamic';

export default function CartPage() {
    const { cartItems, subtotal, removeFromCart, updateQuantity } = useCart();
    return (
        <main className={'flex flex-col items-center justify-start gap-8 py-20 w-2/3'}>
            <span className={'font-prata text-[28px] w-full'}>
                Shopping Cart <br/>
                <span className={'font-plus-jakarta-sans text-[18px]'}>
                    {cartItems.length} item{cartItems.length > 1 && 's'} in your cart
                </span>
            </span>
            <div className={'flex items-start justify-between w-full gap-10'}>
                <div className={'w-full flex flex-col items-center gap-10'}>
                    {cartItems && cartItems.length > 0 &&
                        cartItems.map((item: CartItemType) => (
                            <CartItem
                                key={item.id}
                                item={item}
                                onRemove={removeFromCart}
                                onQuantityChange={updateQuantity}
                            />
                        ))
                    }
                </div>
                <div
                    className={'flex flex-col items-center justify-center p-[10px] gap-6 bg-white h-fit rounded-lg w-fit border-1'}>
                    <span className={'text-[24px] font-prata'}>Order Summary</span>
                    <div className={'flex flex-col font-plus-jakarta-sans text-[20px] min-w-[300px] px-[10px] gap-3'}>
                        <div className={'flex justify-between'}>
                            <span>Subtotal</span>
                            <span>
                                {subtotal.toLocaleString("vi-VN", {
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
                            })}
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
                    <div className={'flex-col flex gap-4 w-full'}>
                        <Link href={'/checkout'}>
                            <Button
                                shape={'rect'}
                                variant={'solid'}
                                disabled={cartItems.length === 0}
                                width={300}
                                height={60}
                                className={'bg-black text-white hover:bg-gray-700 font-plus-jakarta-sans font-light text-[18px]'}>
                                Proceed to Checkout
                            </Button>
                        </Link>
                        <Link href={'/'}>
                            <Button
                                shape={'rect'}
                                variant={'outline'}
                                width={300}
                                height={60}
                                className={'font-plus-jakarta-sans text-[18px]'}>
                                Continue Shopping
                            </Button>
                        </Link>
                    </div>
                </div>
            </div>
        </main>
    )
}