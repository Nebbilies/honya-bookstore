'use client';
import Icon from "@/components/Icon";
import {useUI} from "@/app/(cms)/_context/UIContext";
import {signOut, useSession} from "next-auth/react";
import Link from "next/link";
import {useState} from "react";

export default function CMSHeader() {
    const [isOpen, setIsOpen] = useState(false);
    const session = useSession();
    const { setNavCollapsed } = useUI();

    return (
        <header className="w-full h-[100px] bg-white flex items-center px-6 py-4 shadow-xs justify-between">
            <div onClick={() => setNavCollapsed(prev => !prev)} className="cursor-pointer">
                <Icon name={'collapse-left'} size={30}/>
            </div>
            <div className="relative flex items-center justify-center gap-8">
                <div onClick={() => setIsOpen(!isOpen)} className={'w-[50px] h-[50px] rounded-full bg-cover bg-center cursor-pointer'}
                     style={{backgroundImage: `url('/images/avatarPlaceholder.png')`}}/>
                <div
                    className={`absolute right-0 top-14 w-48 bg-white border border-gray-200 rounded-md shadow-lg z-50 ${isOpen ? 'block' : 'hidden'}`}>
                    <Link href={'https://keycloak.kevinnitro.id.vn/realms/honyabookstore-dev/account'}
                          className="block px-4 py-2 text-gray-800 hover:bg-gray-100">
                        Profile
                    </Link>
                    {(session.data?.role === 'staff' || session.data?.role === 'admin') && (
                        <Link href={'/'}
                              className="block px-4 py-2 text-gray-800 hover:bg-gray-100">
                            Storefront
                        </Link>
                    )}
                </div>
                <button onClick={() => signOut()}>
                    <Icon name={'logout'} size={30} className="cursor-pointer"/>
                </button>
            </div>
        </header>
    );
}