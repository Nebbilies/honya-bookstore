'use client'
import React from "react"
import {isRequiredInputField} from "graphql/type";
import {FieldError} from "react-hook-form";

type ShortFieldProps = {
    label?: string
    hideLabel?: boolean
    helper?: string
    error?: string
    className?: string
    charLimit?: number
    onValueChange?: (e: React.ChangeEvent<HTMLInputElement>) => void
} & React.InputHTMLAttributes<HTMLInputElement>

export default function ShortField({
                                       label,
                                       hideLabel,
                                       helper,
                                       error,
                                       className,
                                       charLimit,
                                       onValueChange,
                                       ...props
                                   }: ShortFieldProps) {
    return (
        <div {...props} className="flex flex-col font-plus-jakarta-sans w-full">
            {!hideLabel && label && (
                <label className="mb-1 text-[16px] text-black">
                    {label}
                    {props.required && <span className="text-red-500 ml-1">*</span>}
                </label>
            )}
            <input
                value={props.value}
                maxLength={charLimit}
                onChange={onValueChange}
                placeholder={props.placeholder}
                className={`border rounded-[10px] focus:outline-none focus:border-blue-500 border-line-color transition ${
                    props.disabled
                        ? "bg-disabled-color text-gray-500 cursor-not-allowed"
                        : "bg-transparent"
                } ${className || ""}`}
            />
            {helper && <span className="text-xs text-gray-500 mt-1">{helper}</span>}
            {error && <span className="text-xs text-red-500 mt-1">{error}</span>}
        </div>
    )
}
