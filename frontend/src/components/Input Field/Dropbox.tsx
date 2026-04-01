import React from "react"

type DropboxProps = {
    label?: string
    hideLabel?: boolean
    helper?: string
    error?: string
    className?: string
    options: string[]
    onValueChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void
} & React.SelectHTMLAttributes<HTMLSelectElement>

export default function Dropbox({
                                    label,
                                    hideLabel,
                                    helper,
                                    error,
                                    className,
                                    options,
                                    onValueChange,
                                    ...props
                                }: DropboxProps) {
    return (
        <div className="flex flex-col font-plus-jakarta-sans w-full">
            {!hideLabel && label && (
                <label className="mb-1 text-[16px] text-black">{label}</label>
            )}
            <select
                {...props}
                onChange={onValueChange}
                defaultValue={""}
                className={`border rounded-[10px] focus:border-[#ffffff] border-line-color transition ${
                    props.disabled
                        ? "bg-disabled-color text-gray-500 cursor-not-allowed"
                        : "bg-transparent"
                } ${className || ""}`}
            >
                <option value="" disabled>Select an option</option>
                {options.map((opt, idx) => (
                    <option key={idx} value={opt}>
                        {opt}
                    </option>
                ))}
            </select>
            {helper && <span className="text-xs text-gray-500 mt-1">{helper}</span>}
            {error && <span className="text-xs text-red-500 mt-1">{error}</span>}
        </div>
    )
}
