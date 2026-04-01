import { toast } from "sonner";

interface PresignedUrlResponse {
    url: string;
    key: string;
}

export const uploadImage = async (file: File, token: string) => {
    const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/media/images/upload-url`,
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        },
    );
    if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || "Failed to get upload URL");
    }
    const data: PresignedUrlResponse = await res.json();
    const uploadRes = await fetch(data.url, {
        method: "PUT",
        headers: {
            "Content-Type": file.type,
        },
        body: file,
    });
    if (!uploadRes.ok) {
        toast.error("Failed to upload image");
        throw new Error("Failed to upload image to storage");
    }

    const createMediaRes = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/media`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
            key: data.key,
            order: 1,
            altText: file.name,
        }),
    });
    if (!createMediaRes.ok) {
        const err = await createMediaRes.json();
        throw new Error(err.message || "Failed to create media record");
    };

    const createdMedia = await createMediaRes.json();
    return {
        key: data.key,
        url: data.url.split("?")[0],
        mediaId: createdMedia.id,
        altText: createdMedia.altText,
    };
};
