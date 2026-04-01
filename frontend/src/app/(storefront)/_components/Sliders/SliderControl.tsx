import { Icon } from '@iconify/react';

interface SliderControlProps {
    direction: 'left' | 'right';
    onClick: () => void;
}


export default function SliderControl({ direction, onClick }: SliderControlProps) {
    return (
        <button
            onClick={onClick}
            className={`
                bg-opacity-0 hover:bg-white transition-colors duration-300
                rounded-full p-2 shadow-md bg-transparent shadow-none border-2 border-line-color cursor-pointer
                ${direction === 'left' ? 'mr-2' : 'ml-2'}
            `}>
            <Icon
                icon={direction === 'left' ? 'mdi:chevron-left' : 'mdi:chevron-right'}
                width={48}
                height={48}
            />
        </button>
    )
}