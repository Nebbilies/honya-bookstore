"use client";
import {cn} from "@/lib/utils";
import Icon from "@/components/Icon";
import {Icon as IconifyIcon} from "@iconify/react";
import {IconName} from "@/components/Icon";
import {useState} from "react";

type ButtonVariant = "solid" | "outline" | "ghost";
type ButtonShape = "rect" | "circle" | "square";

type ButtonProps = {
    children?: React.ReactNode;
    variant?: ButtonVariant;
    shape?: ButtonShape;
    icon?: IconName;
    iconPosition?: "left" | "right";
    isLoading?: boolean;
    width?: number | string;
    height?: number | string;
    //fontSize?: number | string;
    iconSize?: number;
    onClick?: () => void | Promise<void>;
    disabled?: boolean;
    className?: string;
} & React.ButtonHTMLAttributes<HTMLButtonElement>;

export default function Button({
                                   children,
                                   variant = "solid",
                                   shape = "rect",
                                   icon,
                                   iconPosition = "left",
                                   isLoading: externalLoading,
                                   width,
                                   height,
                                   //fontSize,
                                   iconSize = 18,
                                   disabled,
                                   onClick,
                                   className,
                                   ...props
                               }: ButtonProps) {
    const [internalLoading, setInternalLoading] = useState(false)
    const loading = externalLoading ?? internalLoading

    async function handleClick() {
        if (!onClick || loading) return
        const result = onClick()
        if (result instanceof Promise) {
            try {
                setInternalLoading(true)
                await result
            } finally {
                setInternalLoading(false)
            }
        }
    }

    const base =
        "flex items-center justify-center transition-all focus:outline-none " +
        "select-none disabled:opacity-60 disabled:cursor-not-allowed align-middle text-base";

    const variants: Record<ButtonVariant, string> = {
        solid: "bg-black text-white hover:bg-gray-800",
        outline: "border border-[#DEDEDE] text-gray-900 hover:bg-gray-100",
        ghost: "text-gray-700 hover:bg-gray-100",
    };

    const shapes: Record<ButtonShape, string> = {
        rect: "rounded-[20px] px-5",
        circle: "rounded-full aspect-square",
        square: "rounded-md aspect-square",
    };

    return (
        <button
            {...props}
            onClick={handleClick}
            disabled={disabled || loading}
            className={cn(
                base,
                variants[variant],
                shapes[shape],
                !disabled && !loading && 'cursor-pointer',
                className
            )}
            style={{
                width,
                height,
                //fontSize,
            }}
        >
            {loading ? (
                <IconifyIcon
                    icon="line-md:loading-twotone-loop"
                    className="animate-spin"
                    width={iconSize}
                    height={iconSize}
                />
            ) : (
                <>
                    {icon && iconPosition === 'left' && (
                        <Icon name={icon} size={iconSize} className={children ? 'mr-2' : ''}/>
                    )}
                    {children}
                    {icon && iconPosition === 'right' && (
                        <Icon name={icon} size={iconSize} className={children ? 'ml-2' : ''}/>
                    )}
                </>
            )}
        </button>
    );
}
