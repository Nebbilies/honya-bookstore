'use client';

import {useState} from "react";
import {usePathname, useRouter, useSearchParams} from "next/navigation";
import ShortField from "@/components/Input Field/ShortField";
import Icon from "@/components/Icon";

export default function BookHeaderOptions() {
    const searchParams = useSearchParams();
    const pathname = usePathname();
    const {replace} = useRouter();
    const [searchTerm, setSearchTerm] = useState(searchParams.get('search')?.toString() || '');

    const handleSearch = () => {
        const params = new URLSearchParams(searchParams);
        if (searchTerm) {
            params.set('search', searchTerm);
        } else {
            params.delete('search');
        }
        replace(`${pathname}?${params.toString()}`);
    }

    return (
        <div className={'flex'}>
            <ShortField className={'w-80 h-[40px] p-3'}
                        placeholder={'Search...'}
                        value={searchTerm}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') handleSearch();
                        }}
                        onValueChange={(e) => setSearchTerm(e.target.value)}/>
            <button onClick={handleSearch}
                    className={'ml-2 border-1 border-line-color hover:bg-gray-200 px-2.5 rounded-lg flex items-center justify-center'}>
                <Icon name={'search'} size={30} className={'text-black'}/>
            </button>
        </div>
    );
}