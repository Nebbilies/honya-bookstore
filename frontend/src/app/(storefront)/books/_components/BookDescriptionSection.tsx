import SectionTitle from "@/app/(storefront)/_components/SectionTitle";

interface  BookDescriptionSectionProps {
    description: string | null;
}

export default function BookDescriptionSection({description}: BookDescriptionSectionProps) {
    return (
        <section className={'w-4/5 flex flex-col gap-10 items-center mx-auto'}>
            <SectionTitle title={'Description'}/>
            <p className={'text-base leading-7 text-paragraph-color text-[18px] max-w-3/5'}>
                {description ? description : 'No description available for this book.'}
            </p>
        </section>
    )
}