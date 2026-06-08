const ENTITIES: Record<string, string> = {
    '&amp;': '&',
    '&lt;': '<',
    '&gt;': '>',
    '&quot;': '"',
    '&#39;': "'",
    '&apos;': "'",
    '&nbsp;': ' ',
    '&mdash;': '—',
    '&ndash;': '–',
    '&hellip;': '…',
    '&rsquo;': '’',
    '&lsquo;': '‘',
    '&ldquo;': '“',
    '&rdquo;': '”',
};

function decodeEntities(text: string): string {
    return text.replace(/&[a-z]+;|&#\d+;/gi, (match) => ENTITIES[match] ?? match);
}

export function stripHtml(html: string): string {
    if (!html) return '';
    return decodeEntities(html.replace(/<[^>]+>/g, ' '))
        .replace(/\s+/g, ' ')
        .trim();
}

export function getExcerpt(html: string, maxLength = 160): string {
    const text = stripHtml(html);
    if (text.length <= maxLength) return text;
    const truncated = text.slice(0, maxLength);
    const lastSpace = truncated.lastIndexOf(' ');
    return `${(lastSpace > 0 ? truncated.slice(0, lastSpace) : truncated).trimEnd()}…`;
}

export function formatArticleDate(iso?: string): string {
    if (!iso) return '';
    const date = new Date(iso);
    if (Number.isNaN(date.getTime())) return '';
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    return `${day}/${month}/${date.getFullYear()}`;
}

export function getCategoryLabel(tags?: string[], max = 2): string {
    if (!tags || tags.length === 0) return '';
    return tags.slice(0, max).join(', ').toUpperCase();
}

export function slugifyHeading(text: string): string {
    return stripHtml(text)
        .toLowerCase()
        .replace(/[^a-z0-9\s-]/g, '')
        .trim()
        .replace(/\s+/g, '-');
}

export interface TocItem {
    id: string;
    text: string;
    level: number;
}

export interface ParsedContent {
    html: string;
    toc: TocItem[];
}

export function parseArticleContent(content: string): ParsedContent {
    const toc: TocItem[] = [];
    if (!content) return { html: '', toc };

    const seen = new Map<string, number>();
    const html = content.replace(
        /<h([1-3])([^>]*)>([\s\S]*?)<\/h\1>/gi,
        (_match, levelStr: string, attrs: string, inner: string) => {
            const level = Number(levelStr);
            const text = stripHtml(inner);
            let id = slugifyHeading(text) || `section-${toc.length + 1}`;
            const count = seen.get(id) ?? 0;
            seen.set(id, count + 1);
            if (count > 0) id = `${id}-${count}`;
            toc.push({ id, text, level });
            return `<h${level}${attrs} id="${id}">${inner}</h${level}>`;
        },
    );

    return { html, toc };
}
