import Image from "next/image";
import {getBookCover} from "@/lib/utils";
import {BookResponse} from "@/types/types";
import BookHeaderOptions from "@/app/(cms)/admin/books/_components/BookHeaderOptions";
import BookTableActions from "@/app/(cms)/admin/books/_components/BookTableActions";
import {Metadata} from "next";
import {CustomPagination} from "@/components/Pagination/CustomPagination";

export const metadata: Metadata = {
    title: 'Book List',
    description: 'Manage the list of books in the bookstore',
}

async function getBooks(search: string, page: number): Promise<BookResponse> {
    const params = new URLSearchParams();
    if (search) params.set('search', search);
    params.set('page', page.toString());
    params.set('limit', '10');

    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/books?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
        cache: 'no-store'
    });

    if (!res.ok) {
        const errorData = await res.json();
        console.error('Failed to fetch books:', errorData.message || 'Unknown error');
        return {
            data: [],
            meta: { totalItems: 0, pageItems: 0, itemsPerPage: 10, totalPages: 0, currentPage: 1 }
        };
    }

    return res.json();
}

export default async function BookListPage({searchParams}: { searchParams: { search?: string, page?: number } }) {
    const params = await searchParams;
    const search = params?.search || '';
    const page = params?.page || 1;
    const booksData: BookResponse = await getBooks(search, page);
    const books = booksData.data;

    return (
        <main className={'flex flex-col gap-6'}>
            <div className={'flex justify-between items-center'}>
                <h1 className={'font-prata text-3xl'}>Book List</h1>
                <BookHeaderOptions/>
            </div>
            <table className="w-full border-collapse rounded-lg overflow-hidden shadow-sm">
                <thead>
                <tr className="bg-gray-100 text-left text-[16px] font-bold text-gray-700">
                    <th className="px-4 py-3 border-r border-gray-200">Cover Page</th>
                    <th className="px-4 py-3 border-r border-gray-200">Title</th>
                    <th className="px-4 py-3 border-r border-gray-200">Author</th>
                    <th className="px-4 py-3 border-r border-gray-200 text-center">Price</th>
                    <th className="px-4 py-3 border-r border-gray-200 text-center">Pages</th>
                    <th className="px-4 py-3 border-r border-gray-200">Category</th>
                    <th className="px-4 py-3 border-r border-gray-200">Publisher</th>
                    <th className="px-4 py-3 border-r border-gray-200 text-center">Stock</th>
                    <th className="px-4 py-3 border-r border-gray-200 text-center">Sold</th>
                    <th className="px-4 py-3 text-center">Actions</th>
                </tr>
                </thead>

                <tbody className="divide-y divide-gray-100 text-sm text-gray-700">
                {books.map((book, index) => (
                    <tr
                        key={index}
                        className="hover:bg-gray-50 transition-colors duration-200"
                    >
                        <td className="px-4 py-3 border-gray-100">
                            <Image src={getBookCover(book.media)} alt={book.title} width={100} height={150}
                                   className="h-[150px] w-[95px] object-contain rounded-md shadow-sm"/>
                        </td>
                        <td className="px-4 py-3 border-gray-100 font-medium">{book.title}</td>
                        <td className="px-4 py-3 border-gray-100">{book.author}</td>
                        <td className="px-4 py-3 border-gray-100 text-center">{book.price.toLocaleString("vi-VN", {
                            style: "currency",
                            currency: "VND",
                        })}</td>
                        <td className="px-4 py-3 border-gray-100 text-center">{book.pagesCount}</td>
                        <td className="px-4 py-3 border-gray-100">{book.categories.map(cat => cat.name).join(', ')}</td>
                        <td className="px-4 py-3 border-gray-100">{book.publisher}</td>
                        <td className="px-4 py-3 border-gray-100 text-center">{book.stockQuantity}</td>
                        <td className="px-4 py-3 border-gray-100 text-center">{book.purchaseCount}</td>
                        <BookTableActions book={book}/>
                    </tr>
                ))}
                </tbody>
            </table>
            <div className={'self-end'}>
                <CustomPagination totalPages={booksData.meta.totalPages}/>
            </div>
        </main>
    )
}