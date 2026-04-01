import React from "react"

type LongFieldProps = {
    label?: string
    hideLabel?: boolean
    helper?: string
    error?: string
    className?: string
    charLimit?: number
    onValueChange?: (e: React.ChangeEvent<HTMLTextAreaElement>) => void
} & React.TextareaHTMLAttributes<HTMLTextAreaElement>

export default function LongField({
                                      label,
                                      hideLabel,
                                      helper,
                                      error,
                                      className,
                                      charLimit,
                                      onValueChange,
                                      ...props
                                  }: LongFieldProps) {
    return (
        <div className="flex flex-col w-full font-plus-jakarta-sans">
            {!hideLabel && label && (
                <label className="mb-1 text-[16px] text-black">{label}</label>
            )}
            <div className="relative w-full leading-none">
                <textarea
                    {...props}
                    maxLength={charLimit}
                    onChange={onValueChange}
                    className={`w-full border rounded-[10px] focus:outline-none leading-normal focus:border-blue-500 border-line-color transition pr-12 ${
                        props.disabled
                            ? "bg-disabled-color text-gray-500 cursor-not-allowed"
                            : "bg-transparent"
                    } ${className || ""}`}
                />
                {charLimit && (
                    <span className="absolute bottom-3 right-3 text-sm text-right text-gray-800">
                        {props.value ? props.value.toString().length : 0}/{charLimit}
                    </span>
                )}
            </div>
            {helper && <span className="text-xs text-gray-500 mt-1">{helper}</span>}
            {error && <span className="text-xs text-red-500 mt-1">{error}</span>}
        </div>
    )
}
