import { Icon as IconifyIcon } from '@iconify/react';

export type IconName =
    | 'share'
    | 'search'
    | 'bag'
    | 'person'
    | 'trash'
    // | 'trash-delete' // trash but red
    | 'cart'
    | 'option-dots'
    | 'calendar'
    | 'list'
    | 'grid'
    | 'facebook'
    | 'twitter'
    | 'instagram'
    | 'linkedin'
    | 'star-empty'
    | 'star-full'
    | 'upvote'
    | 'downvote'
    | 'popout'
    | 'timer'
    | 'delivery'
    | 'handshake'
    | 'right-arrow'
    | 'loading'
    // CMS
    | 'dashboard'
    | 'category'
    | 'book'
    | 'order'
    | 'discount'
    | 'article'
    | 'media'
    | 'collapse-left'
    | 'logout'
    | 'sort'
    | 'edit'
    | 'add'
    | 'confirm'
    | 'italic'
    | 'bold'
    | 'underline'
    | 'font'
    | 'upload'
    | 'ticket'
    | 'coin'
    | 'people'
    | 'heart'
    | 'order-count'
    | 'expand-more'
    | 'expand-less';



const iconMap: Record<IconName, string> = {
    share: 'mdi:share-variant',
    search: 'mdi:magnify',
    bag: 'mdi:shopping-outline',
    person: 'mdi:account-outline',
    trash: 'mdi:trash-can-outline',
    // 'trash-delete'
    cart: 'mdi:cart-outline',
    'option-dots': 'bi:three-dots',
    calendar: 'mingcute:calendar-line',
    list: 'material-symbols:list-rounded',
    grid: 'mingcute:grid-line',
    facebook: 'mdi:facebook',
    twitter: 'mdi:twitter',
    instagram: 'mdi:instagram',
    linkedin: 'mdi:linkedin',
    'star-empty': 'iconamoon:star-light',
    'star-full': 'iconamoon:star-fill',
    upvote: 'bx:upvote',
    downvote: 'bx:downvote',
    loading: 'mdi:loading',
    // CMS
    dashboard: 'mdi:view-dashboard-outline',
    category: 'mdi:shape-outline',
    book: 'bx:book',
    order: 'material-symbols:orders-outline-rounded',
    discount: 'ic:outline-discount',
    article: 'material-symbols:article-outline-rounded',
    media: 'pajamas:media',
    'collapse-left': 'pajamas:collapse-left',
    logout: 'mdi:logout',
    sort: 'icon-park-solid:sort',
    edit: 'uil:edit',
    add: 'gg:add',
    confirm: 'line-md:confirm-circle',
    italic: 'mdi:format-italic',
    bold: 'mdi:format-bold',
    underline: 'mdi:format-underline',
    font: 'mdi:format-font',
    upload: 'material-symbols:upload',
    ticket: 'ph:ticket-bold',
    coin: 'tabler:coin-filled',
    people: 'famicons:people',
    heart: 'mdi:heart',
    'order-count': 'material-symbols:orders-rounded',
    'right-arrow': 'mdi:arrow-right-bold',
    timer: 'mdi:timer-check',
    delivery: 'mdi:truck-delivery',
    handshake: 'mdi:handshake',
    popout: 'oui:popout',
    "expand-more": "ic:round-expand-more",
    "expand-less": "ic:round-expand-less",
};

interface iconProps {
    name: IconName;
    size?: number;
    color?: string;
    className?: string;
}

export default function Icon({ name, size = 24, color, className }: iconProps) {
    return (
        <IconifyIcon
            icon={iconMap[name]}
            width={size}
            height={size}
            color={color}
            className={className}
        />
    );
}