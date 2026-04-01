import PaymentInformation from "@/app/(storefront)/checkout/_components/PaymentInformation";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: 'Payment Information',
    description: 'Enter your payment details to complete your purchase.',
}

export default function PaymentPage() {
    return (
        <PaymentInformation/>
    )
}