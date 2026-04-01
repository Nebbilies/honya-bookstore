'use client';

import {useCallback, useEffect, useState} from "react";
import useSWR from "swr";
import {fetcher} from "@/lib/utils";
import {useSession} from "next-auth/react";
import {Media, MediaResponse} from "@/types/types";
import Icon from "@/components/Icon";
import Image from "next/image";
import {Check} from "lucide-react";
import {BookImageValues} from "@/app/(cms)/admin/books/_components/BookForm";
import FileUploadZone from "@/app/(cms)/admin/media/upload/_components/FileUploadZone";
import {CustomPagination} from "@/components/Pagination/CustomPagination";
import {useSearchParams} from "next/navigation";
import {CustomClientSidePagination} from "@/components/Pagination/CustomClientSidePagination";

interface SelectMediaDialogProps {
    onClose: () => void;
    isOpen: boolean;
    imageType: 'cover' | 'gallery';
    appendImage: (data: BookImageValues) => void;
    removeImage: (mediaId: string) => void;
    currentImages?: BookImageValues[];
    currentCoverImage?: BookImageValues;
}

export default function SelectMediaDialog({ onClose, isOpen, imageType, appendImage, removeImage, currentImages, currentCoverImage }: SelectMediaDialogProps) {
    const searchParams = useSearchParams();
    const [page, setPage] = useState<number>(1);
    const [activeTab, setActiveTab] = useState<'select' | 'upload'>('select');
    const session = useSession();
    const {data, isLoading} = useSWR<MediaResponse>(
        session.data?.accessToken
            ? `${process.env.NEXT_PUBLIC_API_URL}/media?page=${page}&limit=24`
            : null,
        (url: string) => fetcher(url, session.data?.accessToken || ""),
    );

    const escFunction = useCallback((event: { key: string; }) => {
        if (isOpen && event.key === "Escape") {
            onClose();
        }
    }, []);

    useEffect(() => {
        document.addEventListener("keydown", escFunction, false);

        return () => {
            document.removeEventListener("keydown", escFunction, false);
        };
    }, [escFunction]);


    const media: Media[] = data?.data || [];

    return (
        <div className="fixed inset-0 z-50 flex flex-col items-center justify-center">
            <div
                className="absolute inset-0 bg-gray-900 opacity-75"
                onClick={onClose}
            ></div>
            <div className="relative w-14/15 h-14/15 rounded-[20px] py-[30px] px-[40px] bg-white flex flex-col gap-4">
                <button className="absolute top-3 right-5 text-black text-3xl font-bold z-60 cursor-pointer" onClick={onClose}>
                    &times;
                </button>
                <h1 className={'text-3xl font-prata'}>Select Media</h1>
                <div className={'flex gap-6'}>
                    <button
                        type={'button'}
                        className={`${activeTab === 'select' ? 'text-white bg-button-blue' : 'text-black bg-transparent hover:bg-gray-100 border-line-color border-1 cursor-pointer'} px-6 py-2.5 rounded-[15px] font-plus-jakarta-sans text-[18px]`}
                        onClick={() => setActiveTab('select')}>
                        Select Media
                    </button>
                    <button
                        type={'button'}
                        className={`${activeTab === 'upload' ? 'text-white bg-button-blue' : 'text-black bg-transparent hover:bg-gray-100 border-line-color border-1 cursor-pointer'} px-6 py-2.5 rounded-[15px] font-plus-jakarta-sans text-[18px]`}
                        onClick={() => setActiveTab('upload')}>
                        Upload Media
                    </button>
                </div>
                <div className="relative w-full h-4/5 mt-4 bg-white">
                    {activeTab === 'select' && (
                        <SelectMediaTab
                            imageType={imageType}
                            appendImage={appendImage}
                            removeImage={removeImage}
                            isLoading={isLoading}
                            media={media}
                            onClose={onClose}
                            currentImages={currentImages}
                            currentCoverImage={currentCoverImage}
                            totalPages={data?.meta.totalPages}
                            currentPage={page}
                            onPageChange={setPage}
                        />
                    )}
                    {activeTab === 'upload' && <UploadMediaTab/>}
                </div>
            </div>

        </div>
    );
}

interface SelectMediaTabProps {
    imageType: 'cover' | 'gallery';
    appendImage: (data: BookImageValues) => void;
    removeImage: (mediaId: string) => void;
    isLoading: boolean;
    media: Media[];
    onClose: () => void;
    currentImages?: BookImageValues[];
    currentCoverImage?: BookImageValues;
    totalPages?: number;
    currentPage: number;
    onPageChange: (page: number) => void;
}

function SelectMediaTab({imageType, appendImage, removeImage, isLoading, media, onClose, currentImages, currentCoverImage, totalPages, onPageChange, currentPage}: SelectMediaTabProps) {
    const [selectedMediaIds, setSelectedMediaIds] = useState<string[]>(() => {
        if (imageType === 'cover' && currentCoverImage) {
            return [currentCoverImage.mediaId];
        } else if (imageType === 'gallery' && currentImages) {
            return currentImages.map(img => img.mediaId);
        }
        return [];
    });

    const handleMediaClick = (mediaItem: Media) => {
        const isSelected = selectedMediaIds.includes(mediaItem.id);

        if (isSelected) {
            setSelectedMediaIds(prev => prev.filter(id => id !== mediaItem.id));
            removeImage(mediaItem.id);
        } else {
            if (imageType === 'cover') {
                setSelectedMediaIds([mediaItem.id]);
                appendImage({
                    isCover: true,
                    mediaId: mediaItem.id,
                    url: mediaItem.url,
                });
                removeImage(currentCoverImage?.mediaId || '');
            } else {
                setSelectedMediaIds(prev => [...prev, mediaItem.id]);
                appendImage({
                    isCover: false,
                    mediaId: mediaItem.id,
                    url: mediaItem.url,
                });
            }
        }
    };

    const placeholders = Array.from({ length: 24 });

    return (
        <>
            <div className="flex flex-col h-full border-1 border-line-color rounded-[20px] p-5">
                <div className="flex justify-between mb-5 shrink-0">
                    <select
                        className="border-1 border-line-color rounded-[10px] px-4 py-2 text-sm font-plus-jakarta-sans text-gray-700 focus:outline-none focus:border-button-blue">
                        <option>All Date</option>
                    </select>

                    <div className="relative">
                        <input
                            type="text"
                            placeholder="Search media..."
                            className="border-1 border-line-color rounded-[10px] pl-4 pr-10 py-2 text-sm font-plus-jakarta-sans w-64 focus:outline-none focus:border-button-blue"
                        />
                        <Icon name="search"
                              className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5"/>
                    </div>
                </div>

                <div className="flex-1 overflow-y-auto custom-scrollbar pr-2">
                    <div className="grid grid-cols-4 md:grid-cols-6 lg:grid-cols-8 gap-4">
                        {isLoading ? (
                            placeholders.map((_, index) => (
                                <Image src={'/images/fallbackBookImage.png'} alt={'Loading'} key={index} width={120}
                                       height={120}
                                       className="aspect-square rounded-[10px] object-contain w-[120px] h-[120px] bg-gray-200 animate-pulse"/>
                            ))
                        ) : (
                            media.map((item) => {
                                const isSelected = selectedMediaIds.includes(item.id);
                                return (
                                    <div
                                        key={item.id}
                                        onClick={() => handleMediaClick(item)}
                                        className={`aspect-square rounded-[10px] relative cursor-pointer group overflow-hidden border-2 transition-all duration-200 ${isSelected ? 'border-button-blue' : 'border-transparent hover:border-gray-300'}`}
                                    >
                                        <Image
                                            src={item.url}
                                            alt={item.altText || 'Media'}
                                            fill
                                            className="object-cover"
                                            sizes="(max-width: 768px) 25vw, (max-width: 1200px) 16vw, 12vw"
                                        />
                                        <div
                                            className={`absolute inset-0 bg-black/20 transition-opacity duration-200 flex items-center justify-center ${isSelected ? 'opacity-100' : 'opacity-0 group-hover:opacity-100'}`}>
                                            {isSelected && (
                                                <div className="bg-button-blue rounded-full p-1">
                                                    <Check className="w-4 h-4 text-white"/>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                );
                            })
                        )}
                    </div>
                </div>
                <div className={'self-end mt-4'}>
                    {totalPages && <CustomClientSidePagination
                        totalPages={totalPages}
                        onPageChange={onPageChange}
                        currentPage={currentPage}/>
                    }
                </div>
            </div>
        </>
    );
}

function UploadMediaTab() {
    return (
        <div className="flex flex-col h-full">
            <FileUploadZone/>
        </div>
    );
}