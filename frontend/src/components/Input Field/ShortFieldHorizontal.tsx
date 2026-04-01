import React from "react"

type ShortFieldHorizontalProps = {
    label?: string
    hideLabel?: boolean
    helper?: string
    error?: string
    className?: string
    charLimit?: number
    onValueChange?: (e: React.ChangeEvent<HTMLInputElement>) => void
} & React.InputHTMLAttributes<HTMLInputElement>

export default function ShortFieldHorizontal({
                                                 label,
                                                 hideLabel,
                                                 helper,
                                                 error,
                                                 className,
                                                 charLimit,
                                                 onValueChange,
                                                 ...props
                                             }: ShortFieldHorizontalProps) {
    return (
        <div className="flex items-center gap-1 w-full font-plus-jakarta-sans">
            {!hideLabel && label && (
                <label className="text-sm font-medium text-gray-700">
                    {label}
                </label>
            )}
            <div className="flex flex-col w-full">
                <input
                    onChange={onValueChange}
                    {...props}
                    maxLength={charLimit}
                    className={`border rounded-[10px] focus:border-[#ffffff] border-line-color transition ${
                        props.disabled
                            ? "bg-disabled-color text-gray-500 cursor-not-allowed"
                            : "bg-transparent"
                    } ${className || ""}`}
                />
                {helper && <span className="text-xs text-gray-500 mt-1">{helper}</span>}
                {error && <span className="text-xs text-red-500 mt-1">{error}</span>}
            </div>
        </div>
    )
}
