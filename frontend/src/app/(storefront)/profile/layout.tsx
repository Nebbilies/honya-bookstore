import {Metadata} from "next";
import Breadcrumb, {BreadcrumbItemType} from "@/app/(storefront)/_components/Breadcrumb/Breadcrumb";
import ProfileSidebar from "@/app/(storefront)/profile/_components/ProfileSidebar";

export const metadata: Metadata = {
    title: 'My Profile',
    description: 'View your orders and manage your Honya Bookstore account.',
};

const breadcrumbItems: BreadcrumbItemType[] = [
    {label: 'My Profile', href: '/profile'},
];

export default function ProfileLayout({children}: {children: React.ReactNode}) {
    return (
        <main className={'flex w-full max-w-[1000px] mx-auto flex-col gap-6 px-5 pt-4 pb-20'}>
            <Breadcrumb items={breadcrumbItems}/>

            <div className={'flex items-center gap-4'}>
                <span className={'h-px flex-1 bg-line-color'}/>
                <h1 className={'font-prata text-4xl'}>Profile</h1>
                <span className={'h-px flex-1 bg-line-color'}/>
            </div>

            <div className={'flex flex-col gap-6 sm:flex-row'}>
                <ProfileSidebar/>
                <div className={'min-w-0 flex-1'}>{children}</div>
            </div>
        </main>
    );
}
