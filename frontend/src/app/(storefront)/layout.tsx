import Header from "@/app/(storefront)/_components/Header/Header";
import Footer from "@/app/(storefront)/_components/Footer/Footer";
import {CartContextProvider} from "@/app/(storefront)/_context/CartContext";
import { Toaster } from "sonner";
import {OrderContextProvider} from "@/app/(storefront)/_context/OrderContext";
import { Metadata } from "next";

export const metadata: Metadata = {
    title: {
        template: '%s | Honya Bookstore',
        default: 'Honya Bookstore',
    },
    description: 'Your one-stop shop for the latest and greatest books across all genres. Discover your next favorite read with us!',
};

export default function StorefrontLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
      <CartContextProvider>
          <OrderContextProvider>
              <div className="flex flex-col min-h-screen mx-auto w-full">
                  <Toaster position="bottom-right" richColors/>
                  <div className="flex-grow">
                      <Header/>
                      <div className={'flex flex-col items-center'}>
                          {children}
                      </div>
                  </div>
                  <Footer/>
              </div>
          </OrderContextProvider>
      </CartContextProvider>
  );
}