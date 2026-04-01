interface SliderDotsProps {
    count: number
    active: number
    setActive: (index: number) => void
}

export default function SliderDots({ count, active, setActive }: SliderDotsProps) {
    return (
        <div className="flex gap-2 mt-6">
            {Array.from({ length: count }).map((_, i) => (
                <button
                    key={i}
                    onClick={() => setActive(i)}
                    className={`w-4 h-4 rounded-full border flex items-center justify-center cursor-pointer hover:border-gray-300 transition-all duration-200 ${
                        active === i ? "border-gray-300" : "border-gray-100"
                    }`}>
                    <div className={`w-1 h-1 rounded-full ${active === i ? "bg-gray-700" : "bg-gray-400"}`}/>
                </button>
            ))}
        </div>
    )
}
