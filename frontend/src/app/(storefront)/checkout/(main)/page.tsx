import ShippingInformation from "@/app/(storefront)/checkout/_components/ShippingInformation";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: 'Shipping Information',
    description: 'Provide your shipping details to proceed with your order.',
}

export default function CheckoutPage() {
    return (
        <ShippingInformation/>
    )
}