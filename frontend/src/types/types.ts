export interface BookCategory {
    description: string | null,
    id: string;
    name: string;
    slug: string;
}

export interface BookMedia {
    altText: string | null;
    url: string;
    id: string;
    isCover: boolean;
    order: number;
}

export interface Book {
    categories: BookCategory[];
    media: BookMedia[];
    author: string;
    createdAt: string;
    deletedAt: string | null;
    description: string | null;
    id: string;
    pagesCount: number;
    price: number;
    publisher: string;
    purchaseCount: number;
    rating: number;
    stockQuantity: number;
    title: string;
    updatedAt: string;
    weight: number;
    yearPublished: number;
}

export interface Category {
    id: string,
    name: string,
    slug: string,
    description: string | null,
    createdAt: string,
    updatedAt: string,
    deletedAt: string | null,
}

export interface OrderItemBook {
    id: string,
    author: string,
    price: number,
    rating: number,
    stockQuantity: number,
    title: string,
}

export interface OrderItem {
    book: OrderItemBook,
    id: string,
    price: number,
    quantity: number,
}

export interface Order {
    address: string,
    city: string,
    createdAt: string,
    email: string,
    firstName: string,
    id: string,
    isPaid: boolean,
    items: OrderItem[],
    lastName: string,
    paymentUrl: string,
    phone: string
    provider: 'COD' | 'VNPAY' | 'MOMO',
    status: 'Pending' | 'Processing' | 'Shipping' | 'Delivered' | 'Cancelled',
    totalAmount: number,
    updatedAt: string,
    userId: string,
}

export interface Article {
    id: string,
    title: string,
    slug: string,
    content: string,
    tags: string[],
    status: string,
    thumbnailUrl: string,
    publishedAt: string,
    editedAt: string,
    authorId: string,
}

export interface User {
    id: string,
    name: string,
    username: string,
    email: string,
    createdAt: string,
    orderCount: number,
    status: string,
    role: string,
}

export interface Media {
    id: string,
    altText: string | null,
    order: number,
    url: string,
    createdAt: string,
    deletedAt: string | null,
}

export interface Ticket {
    id: string;
    userId: string;
    title: string;
    category: string;
    description: string;
    status: string;
    createdAt: string;
    response: string;
    resolvedAt: string;
    responderId: string;
}

export interface Review {
    id: string;
    bookId: string;
    userId: string;
    rating: number;
    content: string;
    createdAt: string;
    updatedAt: string;
}

export interface CartItemBook {
    author: string | null;
    description: string | null;
    id: string;
    medium: BookMedia[];
    title: string;
    pagesCount: number | null
    price: number;
    publisher: string;
    purchaseCount: number;
    rating: number;
    stockQuantity: number;
    weight: number | null;
    yearPublished: number;
}

export interface CartItem {
    book: CartItemBook;
    id: string;
    quantity: number;
}

export interface Cart {
    id: string,
    items: CartItem[],
    updatedAt: string,
    userId: string,
}

interface ResponseMetadata {
    currentPage: number;
    itemsPerPage: number;
    pageItems?: number;
    totalItems: number;
    totalPages: number;
}

export interface BookResponse {
    data: Book[];
    meta: ResponseMetadata;
}

export interface CategoryResponse {
    data: Category[];
    meta: ResponseMetadata;
}

export interface MediaResponse {
    data: Media[];
    meta: ResponseMetadata;
}

export interface OrderResponse {
    data: Order[];
    meta: ResponseMetadata;
}