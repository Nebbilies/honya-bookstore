'use client'
import {createContext, useContext} from "react";
import {useState, useEffect} from "react";

interface OrderInformation {
    "address": string,
    "city": string,
    "email": string,
    "firstName": string,
    "lastName": string,
    "phone": string,
    "provider": 'COD' | 'ZALOPAY' | 'MOMO' | 'VNPAY',
    "returnUrl": string,
    "userId": string,
}

interface OrderContextType {
    orderInformation: OrderInformation | null;
    setOrderInformation: (info: OrderInformation) => void;
}

const OrderContext = createContext<OrderContextType | undefined>(undefined);

export function OrderContextProvider({children}: {children: React.ReactNode}) {
    const [orderInformationState, setOrderInformationState] = useState<OrderInformation | null>(null);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        try {
            const storedInfo = sessionStorage.getItem("orderInformation");
            if (storedInfo) {
                setOrderInformationState(JSON.parse(storedInfo));
            }
        } catch (error) {
            console.error("Failed to load order information from sessionStorage:", error);
        } finally {
            setIsLoaded(true);
        }
    }, []);

    const setOrderInformation = (info: OrderInformation) => {
        setOrderInformationState(info);
        sessionStorage.setItem("orderInformation", JSON.stringify(info));
    }

    return (
        <OrderContext.Provider value={{orderInformation: orderInformationState, setOrderInformation}}>
            {isLoaded && children}
        </OrderContext.Provider>
    )
}

export function useOrder() {
    const context = useContext(OrderContext);
    if (context === undefined) {
        throw new Error("useOrder must be used within an OrderContextProvider");
    }
    return context;
}