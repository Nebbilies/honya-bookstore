import LayoutClient from "@/app/(storefront)/checkout/_components/LayoutClient";

export default function CheckoutPageLayout({ children } : { children: React.ReactNode }) {
    return (
        <LayoutClient>
            {children}
        </LayoutClient>
    )
}