'use client';

import Link from "next/link";
import {usePathname} from "next/navigation";
import Icon from "@/components/Icon";

export default function ProfileSidebar({accountBaseUrl}: {accountBaseUrl: string}) {
    const pathname = usePathname();
    const ordersActive = pathname === '/profile' || pathname.startsWith('/profile/orders');

    const externalLinks = [
        {label: 'My Profile', href: `${accountBaseUrl}/`},
        {label: 'Change Password', href: `${accountBaseUrl}/#/account-security/signing-in`},
        {label: 'Settings', href: `${accountBaseUrl}/#/account-security/device-activity`},
    ];

    return (
        <nav className={'w-full sm:w-60 shrink-0 rounded-xl border border-line-color bg-white p-2 h-fit'}>
            <Link
                href={'/profile'}
                className={`flex items-center gap-2 rounded-lg px-3 py-2.5 transition-colors ${
                    ordersActive ? 'bg-gray-100 font-medium text-black' : 'text-gray-700 hover:bg-gray-50'
                }`}>
                <Icon name={'order'} size={18}/>
                <span>My Orders</span>
            </Link>
            {externalLinks.map((item) => (
                <a
                    key={item.label}
                    href={item.href}
                    target={'_blank'}
                    rel={'noopener noreferrer'}
                    className={'flex items-center gap-2 rounded-lg px-3 py-2.5 text-gray-700 transition-colors hover:bg-gray-50'}>
                    <Icon name={'popout'} size={18}/>
                    <span>{item.label}</span>
                </a>
            ))}
        </nav>
    );
}
