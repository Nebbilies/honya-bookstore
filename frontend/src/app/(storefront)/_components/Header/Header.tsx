import Icon from '@/components/Icon';
import Link from "next/link";
import Image from "next/image";
import UserDropdown from "@/app/(storefront)/_components/Header/UserDropdown";
import {auth} from "@/auth";

const navigationItems : Record<string, string> = {
    HOME: '/',
    ABOUT: '/about',
    SHOP: '/books',
    ARTICLES: '/articles',
    CONTACT: '/contact',
};

export default async function Header() {
    const session = await auth();
    return (
        <header className={'min-w-full w-full flex flex-col'}>
            <div className={'flex w-full px-60 justify-end py-2'}>
                <div className={'flex gap-10 items-center font-inter text-[12px]'}>
                    {session?.user ? (
                        <UserDropdown/>
                    ) : (
                        <Link href={'/api/auth/signin'}>
                            <div
                                className={'flex w-full gap-1 items-center hover:text-gray-500 transition-colors duration-300 cursor-pointer'}>
                                <Icon name={"person"} size={16}/>
                                <span className={''}>Sign In</span>
                            </div>
                        </Link>
                    )}
                    <div className={'border-l h-4 border-[#e0e0e0]'}/>
                    <Link href={'/cart'}>
                        <div
                            className={'flex w-full gap-1 items-center hover:text-gray-500 transition-colors duration-300 cursor-pointer'}>
                            <Icon name={"cart"} size={16}/>
                            <span className={''}>Cart</span>
                        </div>
                    </Link>
                </div>
            </div>
            <div className={'w-full border-b border-[#e0e0e0]'}/>
            <div className={'w-full flex justify-between px-60'}>
                <Image src={'/logo.svg'} alt={'Logo'} width={160} height={40} className={'py-4'}/>
                <nav className={'flex gap-0 items-center'}>
                    {Object.entries(navigationItems).map(([name, path]) => (
                        <Link
                            key={name}
                            href={path}
                            className={'px-6 h-full font-inter flex items-center text-sm font-medium hover:bg-gray-300 transition-colors duration-300'}
                        >
                            {name}
                        </Link>
                    ))}
                </nav>
            </div>
            <div className={'w-full border-b border-[#e0e0e0]'}/>
        </header>
    );
}