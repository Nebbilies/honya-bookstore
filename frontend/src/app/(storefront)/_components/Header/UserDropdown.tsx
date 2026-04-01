'use client'
import Icon from "@/components/Icon";
import Link from "next/link";
import {useState} from "react";
import {signOut, useSession} from "next-auth/react";

export default function UserDropdown() {
    const session = useSession();
    const [isOpen, setIsOpen] = useState(false);
    return (
        <div className={'relative'}>
            <div onClick={() => setIsOpen(!isOpen)}
                 className={'flex w-full gap-1 items-center hover:text-gray-500 transition-colors duration-300 cursor-pointer'}>
                <Icon name={"person"} size={16}/>
                <span className={''}>Account</span>
            </div>
            <div
                className={`absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-md shadow-lg z-50 ${isOpen ? 'block' : 'hidden'}`}>
                <Link href={'https://keycloak.kevinnitro.id.vn/realms/honyabookstore-dev/account'}
                      className="block px-4 py-2 text-gray-800 hover:bg-gray-100">
                    Profile
                </Link>
                {(session.data?.role === 'staff' || session.data?.role === 'admin') && (
                    <Link href={'/admin'}
                            className="block px-4 py-2 text-gray-800 hover:bg-gray-100">
                        Admin Panel
                    </Link>
                )}
                <button onClick={() => signOut()}
                      className="block px-4 py-2 text-gray-800 hover:bg-gray-100 w-full text-left cursor-pointer">
                    Logout
                </button>
            </div>
        </div>
    )
}