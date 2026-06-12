import SectionTitle from "@/app/(storefront)/_components/SectionTitle";

interface  BookDescriptionSectionProps {
    description: string | null;
}

export default function BookDescriptionSection({description}: BookDescriptionSectionProps) {
    const paragraphs = description
        ?.split(/\n+/)
        .map((p) => p.trim())
        .filter(Boolean) ?? [];

    return (
        <section className={'w-4/5 flex flex-col gap-10 items-center mx-auto'}>
            <SectionTitle title={'Description'}/>
            <div className={'flex flex-col gap-4 max-w-3/5'}>
                {paragraphs.length > 0 ? (
                    paragraphs.map((paragraph, index) => (
                        <p key={index} className={'text-base leading-7 text-paragraph-color text-[18px]'}>
                            {paragraph}
                        </p>
                    ))
                ) : (
                    <p className={'text-base leading-7 text-paragraph-color text-[18px]'}>
                        No description available for this book.
                    </p>
                )}
            </div>
        </section>
    )
}