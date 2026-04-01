import FileUploadZone from "@/app/(cms)/admin/media/upload/_components/FileUploadZone";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: 'Upload Media',
    description: 'Upload new media files to the CMS.',
}

export default function MediaUploadPage() {
    return (
        <main className={'flex flex-col gap-6 h-full'}>
            <h1 className={'font-prata text-3xl'}>Upload Media</h1>
            <FileUploadZone/>
        </main>
    )
}