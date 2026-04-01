'use client';
import {useDropzone} from "react-dropzone";
import {useState, useCallback} from "react";
import Icon from "@/components/Icon";
import {uploadImage} from "@/lib/services/upload-image";
import {useSession} from "next-auth/react";
import {toast} from "sonner";

export default function FileUploadZone() {
    const [loading, setLoading] = useState(false);
    const session = useSession();
    const token = session.data?.accessToken;

    const onDrop = useCallback((acceptedFiles: File[]) => {
        if (acceptedFiles.length === 0 || loading) return;
        setLoading(true);
        const formData = new FormData();
        acceptedFiles.forEach((file) => {
            formData.append('files', file);
        });
        try {
            const uploadPromises = acceptedFiles.map(async (file) => {
                if (!token) throw new Error('No access token');
                return await uploadImage(file, token);
            });
            Promise.all(uploadPromises)
                .then((results) => {
                    toast.success(`Upload ${results.length} file(s) successfully`);
                })
        } catch {
            toast.error('Failed to upload files');
        } finally {
            setLoading(false);
        }
    }, [loading, token])

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        accept: {
            'image/png' : ['.png'],
            'image/jpeg' : ['.jpeg', '.jpg'],
            'image/gif' : ['.gif'],
        },
        disabled: loading || !token,
        noClick: loading,
        noDrag: loading,
    })

    return (
            <div
                {...getRootProps()}
                className={`w-full flex flex-col items-center justify-center border-2 border-dashed rounded-2xl bg-white p-6 
        transition text-center h-full
        ${
                    loading
                        ? 'opacity-60 cursor-not-allowed'
                        : 'cursor-pointer hover:border-gray-400'
                }
        ${
                    isDragActive && !loading
                        ? 'border-gray-400 bg-gray-50'
                        : 'border-gray-300'
                }`}
            >
                <input {...getInputProps()} disabled={loading}/>

                {loading ? (
                    <>
                        <Icon name={'loading'} size={32} className="w-8 h-8 mb-3 text-gray-500 animate-spin"/>
                        <p className="text-gray-700">Uploading...</p>
                    </>
                ) : (
                    <>
                        <Icon name={'upload'} size={48} className="w-12 h-12 mb-3 text-gray-500"/>
                        <p className="text-lg font-medium text-gray-700">
                            {isDragActive ? 'Drop files here' : 'Drag files here to upload'}
                        </p>
                        <p className="text-sm text-gray-400 mt-1">
                            Supports .png, .jpg, .jpeg, .gif & bulk upload
                        </p>
                    </>
                )}
            </div>
    )
}