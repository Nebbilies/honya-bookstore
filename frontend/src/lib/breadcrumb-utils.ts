export interface BreadcrumbSegment {
    name: string;
    href: string;
}

export function generateBreadcrumbs(pathname: string): BreadcrumbSegment[] {
    const paths = pathname.split("/").filter(Boolean);
    return paths.map((path, index) => ({
        name: decodeURIComponent(path)
            .replace(/-/g, " ")
            .replace(/\b\w/g, (char) => char.toUpperCase()),
        href: "/" + paths.slice(0, index + 1).join("/")
    }));
}