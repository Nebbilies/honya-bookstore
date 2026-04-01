interface SectionTitleProps{
    title: string;
    helper?: string;
}

export default function SectionTitle({ title, helper }: SectionTitleProps) {
    return (
        <div className={'w-full flex flex-col items-center justify-center gap-[12px]'}>
            {helper && (
                <div className={'w-full text-center font-plus-jakarta-sans tracking-widest text-[15px] opacity-70'}>
                    {helper}
                </div>
            )}
            <div className={'flex items-center w-full justify-between gap-4'}>
                <div className={'flex-1 border-1 border-line-color'}></div>
                <span className={'text-[40px] font-prata'}>{title}</span>
                <div className={'flex-1 border-1 border-line-color'}></div>
            </div>
        </div>
    )
}