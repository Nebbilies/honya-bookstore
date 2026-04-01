import { auth } from "@/auth"; // Server-side auth
import { CategoryResponse } from "@/types/types";
import CategoryForm from "@/app/(cms)/admin/categories/_components/CategoryForm";
import CategoryTableOptions from "@/app/(cms)/admin/categories/_components/CategoryTableOptions";
import {CustomPagination} from "@/components/Pagination/CustomPagination";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: 'Category List',
    description: 'Manage the list of categories in the bookstore',
}

export async function getCategories(page: number = 1, limit?: number) {
    const session = await auth();
    const params = new URLSearchParams();
    params.set('page', page.toString());
    if (limit) params.set('limit', limit.toString());

    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/categories?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            authorization: `Bearer ${session?.accessToken}`,
        },
        cache: 'no-store'
    });

    if (!res.ok) {
        const errorData = await res.json();
        console.error('Failed to fetch categories:', errorData.message || 'Unknown error');
        return {
            data: [] ,
            meta: { totalItems: 0, pageItems: 0, itemsPerPage: limit, totalPages: 0, currentPage: page }
        };
    }

    return res.json();
}

export default async function CategoryListPage({searchParams}: { searchParams: { page?: string } }) {
    const params = await searchParams;
    const page = Number(params?.page) || 1;
    const categoriesData: CategoryResponse = await getCategories(page, 8);
    const categories = categoriesData.data || [];

    return (
        <main className={'flex flex-col gap-6'}>
            <h1 className={'font-prata text-3xl'}>Category List</h1>
            <section className={'flex items-start justify-between gap-12 h-full'}>
                <CategoryForm mode="add" />
                <div className={'flex-col flex gap-4 h-full'}>
                    <table className="w-full border-collapse rounded-lg overflow-hidden shadow-sm h-fit">
                        <thead>
                        <tr className="bg-gray-100 text-left text-[16px] font-bold text-gray-700">
                            <th className="px-4 py-3 border-r border-gray-200 text-center">ID</th>
                            <th className="px-4 py-3 border-r border-gray-200">Name</th>
                            <th className="px-4 py-3 border-r border-gray-200">Slug</th>
                            <th className="px-4 py-3 border-r border-gray-200 text-center line-clamp-3">Description</th>
                            <th className="px-4 py-3 text-center">Actions</th>
                        </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 text-sm text-gray-700">
                        {categories.length === 0 ? (
                            <tr>
                                <td colSpan={5} className="text-center py-4">No categories found</td>
                            </tr>
                        ) : (
                            categories.map((category, index) => (
                                <tr
                                    key={category.id || index}
                                    className="hover:bg-gray-50 transition-colors duration-200"
                                >
                                    <td className="px-4 py-3 border-gray-100 font-medium text-center">{category.id}</td>
                                    <td className="px-4 py-3 border-gray-100">{category.name}</td>
                                    <td className="px-4 py-3 border-gray-100">{category.slug}</td>
                                    <td className="px-4 py-3 border-gray-100 max-w-[400px]">{category.description}</td>
                                    <td className="px-4 py-3 text-center">
                                        <CategoryTableOptions category={category}/>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                    <div className={'self-end'}>
                        <CustomPagination totalPages={categoriesData.meta.totalPages}/>
                    </div>
                </div>
            </section>
        </main>
    )
}