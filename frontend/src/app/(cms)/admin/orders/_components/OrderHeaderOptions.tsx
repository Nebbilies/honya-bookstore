'use client';

import {useState} from "react";
import {usePathname, useRouter, useSearchParams} from "next/navigation";
import ShortField from "@/components/Input Field/ShortField";
import Icon from "@/components/Icon";

const STATUS_OPTIONS = ['PENDING', 'PROCESSING', 'SHIPPING', 'DELIVERED', 'CANCELLED'];

export default function OrderHeaderOptions() {
    const searchParams = useSearchParams();
    const pathname = usePathname();
    const {replace} = useRouter();
    const [searchTerm, setSearchTerm] = useState(searchParams.get('search')?.toString() || '');
    const status = searchParams.get('status')?.toString() || '';

    const updateParams = (mutate: (params: URLSearchParams) => void) => {
        const params = new URLSearchParams(searchParams);
        mutate(params);
        params.delete('page');
        replace(`${pathname}?${params.toString()}`);
    };

    const handleSearch = () => {
        updateParams((params) => {
            if (searchTerm) params.set('search', searchTerm);
            else params.delete('search');
        });
    };

    const handleStatusChange = (value: string) => {
        updateParams((params) => {
            if (value) params.set('status', value);
            else params.delete('status');
        });
    };

    return (
        <div className={'flex items-center gap-2'}>
            <select
                value={status}
                onChange={(e) => handleStatusChange(e.target.value)}
                className={'h-[40px] border-1 border-line-color rounded-lg px-3 bg-white cursor-pointer'}>
                <option value={''}>All Status</option>
                {STATUS_OPTIONS.map((option) => (
                    <option key={option} value={option}>
                        {option.charAt(0) + option.slice(1).toLowerCase()}
                    </option>
                ))}
            </select>
            <ShortField className={'w-80 h-[40px] p-3'}
                        placeholder={'Search recipient...'}
                        value={searchTerm}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') handleSearch();
                        }}
                        onValueChange={(e) => setSearchTerm(e.target.value)}/>
            <button onClick={handleSearch}
                    className={'border-1 border-line-color hover:bg-gray-200 px-2.5 h-[40px] rounded-lg flex items-center justify-center'}>
                <Icon name={'search'} size={28} className={'text-black'}/>
            </button>
        </div>
    );
}
