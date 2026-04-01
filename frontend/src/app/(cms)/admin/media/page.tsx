import {getSession} from "next-auth/react";
import {MediaResponse} from "@/types/types";
import Image from "next/image";
import MediaHeaderOptions from "@/app/(cms)/admin/media/upload/_components/MediaHeaderOptions";
import MediaTableOptions from "@/app/(cms)/admin/media/upload/_components/MediaTableOptions";
import {auth} from "@/auth";
import {CustomPagination} from "@/components/Pagination/CustomPagination";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: 'Media Library',
    description: 'Manage the media library',
}

async function getMedia(search: string, page: number = 1, limit: number = 10) {
    const session = await auth();
    const params = new URLSearchParams();
    if (search) params.set('search', search);
    params.set('page', page.toString());
    params.set('limit', limit.toString());

    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/media?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            authorization: `Bearer ${session?.accessToken}`,
        },
        cache: 'no-store'
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to fetch media');
    }

    return res.json();
}

export default async function MediaLibraryPage({searchParams}: { searchParams: { search?: string, page?: string } }) {
    const params = await searchParams;
    const search = params?.search || '';
    const page = Number(params?.page) || 1;

    const mediaData: MediaResponse = await getMedia(search, page);
    const mediaList = mediaData.data || [];

    return (
        <main className={'flex flex-col gap-6'}>
            <div className={'flex justify-between items-center py-2'}>
                <h1 className={'font-prata text-3xl'}>Media Library</h1>
                <MediaHeaderOptions />
            </div>

            <table className="w-full border-collapse rounded-lg overflow-hidden shadow-sm">
                <thead>
                <tr className="bg-gray-100 text-left text-[16px] font-bold text-gray-700">
                    <th className="px-4 py-3 border-r border-gray-200">Thumbnail</th>
                    <th className="px-4 py-3 border-r border-gray-200">Name</th>
                    <th className="px-4 py-3 border-r border-gray-200">Date Uploaded</th>
                    <th className="px-4 py-3 text-center">Actions</th>
                </tr>
                </thead>

                <tbody className="divide-y divide-gray-100 text-sm text-gray-700">
                {mediaList.length === 0 ? (
                    <tr>
                        <td colSpan={4} className="text-center py-10 text-gray-500">No media found</td>
                    </tr>
                ) : (
                    mediaList.map((item, index) => (
                        <tr
                            key={item.id || index}
                            className="hover:bg-gray-50 transition-colors duration-200"
                        >
                            <td className="px-4 py-3 border-gray-100">
                                <div className="relative w-24 h-16">
                                    <Image
                                        src={item.url}
                                        alt={item.altText || 'media'}
                                        fill
                                        className="object-cover rounded-md shadow-sm"
                                    />
                                </div>
                            </td>
                            <td className="px-4 py-3 border-gray-100 font-medium">{item.altText}</td>
                            <td className="px-4 py-3 border-gray-100">
                                {new Date(item.createdAt).toLocaleString()}
                            </td>
                            <td className="px-4 py-3 text-center">
                                <MediaTableOptions mediaId={item.id} />
                            </td>
                        </tr>
                    ))
                )}
                </tbody>
            </table>
            <div className={'self-end'}>
                <CustomPagination totalPages={mediaData.meta.totalPages}/>
            </div>
        </main>
    )
}