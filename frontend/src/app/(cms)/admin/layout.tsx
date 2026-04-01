import NavigationBar from "@/app/(cms)/_components/NavigationBar";
import CMSHeader from "@/app/(cms)/_components/Header";
import {UIProvider} from "@/app/(cms)/_context/UIContext";
import CMSFooter from "@/app/(cms)/_components/Footer";
import Breadcrumb from "@/app/(storefront)/_components/Breadcrumb/Breadcrumb";
import {headers} from "next/headers";
import {Toaster} from "sonner";
import {ConfirmationProvider} from "@/app/(cms)/_context/AlertDialogContext";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: {
        template: '%s | CMS - Honya Bookstore',
        default: 'CMS - Honya Bookstore',
    },
    description: 'Content Management System for Honya Bookstore',
};


export default async function CMSLayout({
    children,
}: {
    children: React.ReactNode
}) {
    const headersList = await headers();
    const pathname = await headersList.get('x-url') || '/admin';

    const breadcrumbItems = pathname.split('/').filter(Boolean).map((segment, index, arr) => {
        const path = '/' + arr.slice(0, index + 1).join('/');
        return { label: segment.charAt(0).toUpperCase() + segment.slice(1), href: path };
    });

    return (
        <UIProvider>
            <ConfirmationProvider>
                <div className="flex h-screen w-screen overflow-hidden bg-page-background">
                    <NavigationBar/>
                    <div className={'flex flex-col justify-between w-full h-full'}>
                        <Toaster position="bottom-right" richColors/>
                        <CMSHeader/>
                        <div className={'mt-4 ml-6'}>
                            <Breadcrumb items={breadcrumbItems}/>
                        </div>
                        <main className="m-6 mt-4 flex-1 flex flex-col gap-4 overflow-y-auto">
                            <div className={'overflow-y-scroll bg-white rounded-lg shadow-md p-6 min-h-full'}>
                                {children}
                            </div>
                        </main>
                        <CMSFooter/>
                    </div>
                </div>
            </ConfirmationProvider>
        </UIProvider>
    );
}