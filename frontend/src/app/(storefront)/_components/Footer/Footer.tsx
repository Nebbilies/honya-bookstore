import {Span} from "next/dist/server/lib/trace/tracer";
import Icon from "@/components/Icon";

export default function Footer() {
    return (
        <footer className={'w-full h-auto flex flex-col bg-background-darker'}>
            <div className={'w-full flex justify-between items-center px-100 py-10 '}>
                <div className={'font-cormorant text-5xl'}>
                    <span className={'font-bold'}>BOOK</span>
                    <span className={'font-light'}>STORE</span>
                </div>
                <div className={'font-cormorant text-4xl flex flex-col items-center justify-center opacity-55'}>
                    <span className={'font-bold'}>CONTACT <span className={'font-normal'}>US</span> </span>
                    <div className={'flex items-center justify-between gap-6 mt-4'}>
                        <Icon name={"facebook"} size={24} className={"cursor-pointer hover:opacity-70 transition-opacity duration-300"}/>
                        <Icon name={"twitter"} size={24} className={"cursor-pointer hover:opacity-70 transition-opacity duration-300"}/>
                        <Icon name={"instagram"} size={24} className={"cursor-pointer hover:opacity-70 transition-opacity duration-300"}/>
                        <Icon name={"linkedin"} size={24} className={"cursor-pointer hover:opacity-70 transition-opacity duration-300"}/>
                    </div>
                </div>
            </div>
            <div className={'w-full text-center opacity-50 text-md font-inter py-1'}>
                @ 2025 UIT. SE357 Course Project.
            </div>
        </footer>
    );
}