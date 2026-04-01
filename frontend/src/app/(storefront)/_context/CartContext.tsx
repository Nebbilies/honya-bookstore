'use client'

import {createContext, useContext, useState, useMemo, ReactNode, useEffect, useRef} from "react";
import {Book, CartItem} from "@/types/types";
import {useSession} from "next-auth/react";
import {toast} from "sonner";

interface CartContextType {
    cartItems: CartItem[];
    cartId: string;
    subtotal: number;
    addToCart: (book: Book) => Promise<void>;
    removeFromCart: (itemId: string) => Promise<void>;
    updateQuantity: (itemId: string, quantity: number) => void;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export function CartContextProvider({children}: {children: ReactNode}) {
    const session = useSession();
    const [cartItems, setCartItems] = useState<CartItem[]>([]);
    const [cartId, setCartId] = useState<string>('');

    useEffect(() => {
        if (session.status === 'unauthenticated' || !session.data?.accessToken) {
            return;
        }

        const fetchCart = async () => {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/cart/me`, {
                headers: {
                    authorization: `Bearer ${session.data?.accessToken}`
                }
            });
            if (res.status === 404) {
                const createRes = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/cart`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        authorization: `Bearer ${session.data?.accessToken}`
                    },
                    body: JSON.stringify({ userId: session.data?.user?.id })
                });
                const newCart = await createRes.json();
                setCartId(newCart.id);
                setCartItems(newCart.items);
            } else if (res.ok) {
                const cartData = await res.json();
                setCartId(cartData.id);
                setCartItems(cartData.items);
            } else {
                console.error('Failed to fetch cart data');
            }
        };
        fetchCart();
    }, [session.data?.accessToken, session.data?.userId]);

    const subtotal = useMemo(() => {
        return cartItems.reduce((total, item) => total + item.book.price * item.quantity, 0);
    }, [cartItems]);

    const addToCart = async (book: Book) => {
        const existingItem = cartItems.find((item) => item.id === book.id);
        if (existingItem) {
            updateQuantity(existingItem.id, existingItem.quantity + 1);
        } else {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/cart/${cartId}/items`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session.data?.accessToken}`
                },
                body: JSON.stringify({
                    bookId: book.id,
                    quantity: 1
                })
            });
            if (!res.ok) {
                throw new Error('Failed to add item to cart');
            }
            const newCart = await res.json();
            setCartItems(newCart.items);
            toast.success('Item added to cart successfully');
        }
    }

    const removeFromCart = async (itemId: string) => {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/cart/${cartId}/items/${itemId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                authorization: `Bearer ${session.data?.accessToken}`
            }
        });
        if (!res.ok) {
            throw new Error('Failed to remove item from cart');
        }
        setCartItems((prevItems) => prevItems.filter((item) => item.id !== itemId));
        toast.success('Item removed from cart successfully');
    }

    const debounceTimeouts = useRef<{[key: string]: NodeJS.Timeout}>({});

    const updateQuantity = (itemId: string, newQuantity: number) => {
        setCartItems((prevItems) => {
            return prevItems.map((item) =>
                item.id === itemId ? {...item, quantity: newQuantity} : item
            );
        })

        if (debounceTimeouts.current[itemId]) {
            clearTimeout(debounceTimeouts.current[itemId]);
        }

        // debounce
        debounceTimeouts.current[itemId] = setTimeout(async () => {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/cart/${cartId}/items/${itemId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session.data?.accessToken}`
                },
                body: JSON.stringify({
                    quantity: newQuantity
                })
            });
            if (!res.ok) {
                throw new Error('Failed to update item quantity');
            }
            toast.success('Cart updated successfully');

            // clean up
            delete debounceTimeouts.current[itemId];
        }, 1000);
    }

    const value = {
        cartItems,
        cartId,
        subtotal,
        addToCart,
        removeFromCart,
        updateQuantity
    }

    return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export const useCart = () => {
    return useContext(CartContext) as CartContextType;
}