'use client';
import {CartItem as CartItemType} from "@/types/types";
import Image from "next/image";
import Button from "@/components/Button";
import {getBookCover} from "@/lib/utils";

interface CartItemProps {
    item: CartItemType;
    onQuantityChange: (itemId: string, newQuantity: number) => void;
    onRemove: (itemId: string) => void;
}

export default function CartItem({item, onQuantityChange, onRemove}: CartItemProps) {
    return (
        <div className={'flex w-full items-start gap-6 relative bg-white px-[15px] py-[12px] rounded-lg shadow-md'}>
            <Image src={getBookCover(item.book.medium)} alt={"cover"} width={200} height={350} className={'rounded-lg h-[200px] w-auto shadow-lg'} />
            <div className={'flex flex-col gap-4 font-prata'}>
                <span className={'text-[24px] line-clamp-1'}>{item.book.title}</span>
                <span className={'font-plus-jakarta-sans text-[20px]'}>by {item.book.author}</span>
                <span className={'text-price-color text-[24px]'}>{item.book.price.toLocaleString("vi-VN", {
                    style: "currency",
                    currency: "VND",
                })}</span>
                <div className={'flex items-center justify-between w-[120px] border-line-color border-1'}>
                    <Button
                        width={36}
                        height={36}
                        onClick={() => {
                            if (item.quantity > 1) {
                                onQuantityChange(item.id, item.quantity - 1)
                            } else {
                                onRemove(item.id)
                            }
                        }}
                        shape={'square'}
                        variant={'outline'}
                        // if quantity is 1 show trash icon else no icon field
                        icon={item.quantity === 1 ? 'trash' : undefined}
                        className={'rounded-none text-[35px] font-bold border-0 border-r-1'}>{item.quantity !== 1 && '-'}</Button>
                    {item.quantity}
                    <Button
                        width={36}
                        height={36}
                        onClick={() => onQuantityChange(item.id, item.quantity + 1)}
                        shape={'square'}
                        variant={'outline'}
                        className={'rounded-none text-[30px] font-bold border-0 border-l-1'}>+</Button>
                </div>
            </div>
        </div>
    )
}