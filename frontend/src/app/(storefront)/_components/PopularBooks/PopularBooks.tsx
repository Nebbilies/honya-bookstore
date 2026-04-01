import {Book, Category, CategoryResponse} from "@/types/types";
import SectionTitle from "@/app/(storefront)/_components/SectionTitle";
import CategorySelector from "@/app/(storefront)/_components/PopularBooks/CategorySelector";

interface PopularBooksProps {
    books: Book[];
}

export default async function PopularBooks({ books }: PopularBooksProps) {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/categories`, {cache: "no-store"});
    const categoriesData: CategoryResponse = await res.json();
    const categories: Category[] = categoriesData.data;
    categories.unshift({
        id: 'all',
        name: 'All',
        description: 'All Books',
        slug: 'all',
        createdAt: 'a',
        updatedAt: 'a',
        deletedAt: null,
    });

    return (
        <section className="flex flex-col items-center justify-center py-12 gap-[40px]">
            <SectionTitle title={'Popular Books'} helper={'YOU\'LL LOVE THESE'}/>
            <CategorySelector categories={categories} books={books} />
        </section>
    );
}
