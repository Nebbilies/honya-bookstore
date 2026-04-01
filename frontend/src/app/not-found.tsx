import Link from 'next/link'

export default function NotFound() {
    return (
        <div className={'w-screen h-screen flex flex-col gap-4 justify-center items-center font-plus-jakarta-sans'}>
            <h2 className={'text-3xl'}>Not Found</h2>
            <p className={'text-xl'}>Could not find requested resource</p>
            <Link href="/" className={'text-xl text-blue-500'}>Return Home</Link>
        </div>
    )
}