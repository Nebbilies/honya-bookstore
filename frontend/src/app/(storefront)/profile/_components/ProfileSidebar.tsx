'use client';

import Link from "next/link";
import {usePathname} from "next/navigation";
import Icon from "@/components/Icon";

const KEYCLOAK_ACCOUNT = 'https://keycloak.kevinnitro.id.vn/realms/honyabookstore-dev/account';

const EXTERNAL_LINKS = [
    {label: 'My Profile', href: `${KEYCLOAK_ACCOUNT}/#/personal-info`},
    {label: 'Change Password', href: `${KEYCLOAK_ACCOUNT}/#/security/signingin`},
    {label: 'Settings', href: `${KEYCLOAK_ACCOUNT}/#/security/device-activity`},
];

export default function ProfileSidebar() {
    const pathname = usePathname();
    const ordersActive = pathname === '/profile' || pathname.startsWith('/profile/orders');

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
            {EXTERNAL_LINKS.map((item) => (
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
