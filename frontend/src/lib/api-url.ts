function trimTrailingSlash(value: string): string {
  return value.replace(/\/+$/, "");
}

function joinUrl(baseUrl: string, path: string): string {
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  return `${trimTrailingSlash(baseUrl)}${normalizedPath}`;
}

export function publicApiUrl(path: string): string {
  if (!process.env.NEXT_PUBLIC_API_URL) {
    throw new Error("NEXT_PUBLIC_API_URL is not configured");
  }
  return joinUrl(process.env.NEXT_PUBLIC_API_URL, path);
}

export function serverApiUrl(path: string): string {
  const baseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
  if (!baseUrl) {
    throw new Error("API_INTERNAL_URL or NEXT_PUBLIC_API_URL must be configured");
  }
  return joinUrl(baseUrl, path);
}
