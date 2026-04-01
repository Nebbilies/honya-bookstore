import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"
import {BookMedia} from "@/types/types";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function getBookCover(media: BookMedia[]) {
    const cover = media.find((med) => med.isCover);
    return cover ? cover.url : "/images/fallbackBookImage.png"
}

export const fetcher = (
    url: string,
    token: string,
) =>
    fetch(url, {
        headers: {
            "Content-Type": "application/json",
            authorization: `Bearer ${token}`,
        },
    }).then((res) => {
        if (!res.ok) {
            throw new Error(`Failed to fetch ${url}`);
        }
        return res.json();
    });