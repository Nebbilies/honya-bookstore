'use client';
import {useState} from "react";
import Image from "next/image";
import Icon, {IconName} from "@/components/Icon";
import {motion} from "framer-motion";
import { useUI } from "../_context/UIContext";

interface NavItem {
    label: string,
    icon?: IconName,
    href: string | null,
    type: 'main' | 'sub' | 'parent',
}

const navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', href: '/admin', type: 'main' },
    { label: 'Category' , icon: 'category', href: '/admin/categories', type: 'main' },
    { label: 'Book', icon: 'book', href: null, type: 'parent' },
    { label: 'Book List', href: '/admin/books', type: 'sub' },
    { label: 'Add Book', href: '/admin/books/add', type: 'sub' },
    { label: 'Order', icon: 'order', href: '/admin/orders', type: 'main' },
    { label: 'Discount', icon: 'discount', href: '/admin/discounts', type: 'parent' },
    { label: 'Discount List', href: '/admin/discounts', type: 'sub' },
    { label: 'Add Discount', href: '/admin/discounts/add', type: 'sub' },
    { label: 'Article', icon: 'article', href: null, type: 'parent' },
    { label: 'Article List', href: '/admin/articles', type: 'sub' },
    { label: 'Add Article', href: '/admin/articles/add', type: 'sub' },
    { label: 'Media', icon: 'media', href: '/admin/media', type: 'parent' },
    { label: 'Media Library', href: '/admin/media', type: 'sub' },
    { label: 'Upload Media', href: '/admin/media/upload', type: 'sub' },
    { label: 'Ticket', icon: 'ticket', href: '/admin/tickets', type: 'main' },
    { label: 'Users' , icon: 'people', href: '/admin/users', type: 'main' },
]

export default function NavigationBar() {
    const { isNavCollapsed } = useUI();
    const [isOpen, setIsOpen] = useState<Map<string, boolean>>(
        new Map(navItems.filter(item => item.type === 'parent').map(item => [item.label, false]))
    );
    const toggleSubMenu = (label: string) => {
        setIsOpen(prev => new Map(prev).set(label, !prev.get(label)));
    }

    return (
        <motion.nav className={`${isNavCollapsed ? 'w-0' : 'w-64'} bg-white text-white min-h-screen flex flex-col gap-4`}
            initial={{ width: isNavCollapsed ? 0 : 256 }}
            animate={{ width: isNavCollapsed ? 0 : 256 }}
            transition={{ duration: 0.3, ease: 'easeInOut' }}
        >
            <Image src={'/logo.svg'} alt={'logo'} width={200} height={75} className={'w-auto h-[100px] p-[25px] bg-background-darker'}/>
            <ul className="flex-1 overflow-y-auto font-prata text-[18px]">
                {navItems.map((item, index) => {
                    if (item.type === 'main') {
                        return (
                            <li key={index}>
                                <a href={item.href || '#'} className="flex items-center p-4 text-gray-700 hover:bg-gray-200 transition-all duration-200">
                                    {item.icon && <Icon name={item.icon} size={25} className="mr-3"/>}
                                    {item.label}
                                </a>
                            </li>
                        )
                    } else if (item.type === 'parent') {
                        return (
                            <li key={index}>
                                <button
                                    onClick={() => toggleSubMenu(item.label)}
                                    className="flex items-center justify-between p-4 w-full text-left text-gray-700 hover:bg-gray-200 cursor-pointer transition-all duration-200"
                                >
                                    <div className="flex items-center">
                                        {item.icon && <Icon name={item.icon} size={25} className="mr-3"/>}
                                        {item.label}
                                    </div>
                                    {isOpen.get(item.label) ? <Icon name={'expand-less'} size={30}/> : <Icon name={'expand-more'} size={30}/>}
                                </button>
                                {isOpen.get(item.label) && (
                                    <ul className="bg-gray-100">
                                        {navItems.filter(subItem => subItem.type === 'sub' && navItems.indexOf(subItem) > index && (navItems.indexOf(subItem) < navItems.findIndex(i => i.type === 'parent' && navItems.indexOf(i) > index) || navItems.findIndex(i => i.type === 'parent' && navItems.indexOf(i) > index) === -1)).map((subItem, subIndex) => (
                                            <li key={subIndex}>
                                                <a href={subItem.href || '#'} className="flex items-center p-4 pl-8 text-gray-600 hover:bg-gray-200 transition-all duration-200">
                                                    {subItem.label}
                                                </a>
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </li>
                        )
                    }
                })}
            </ul>
        </motion.nav>
    )
}