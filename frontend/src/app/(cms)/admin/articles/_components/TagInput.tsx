'use client';

import React, { useState } from "react";

const ALLOWED_CHAR = /^[a-zA-Z0-9 -]$/;
const INVALID_CHARS = /[^a-zA-Z0-9 -]/g;

interface TagInputProps {
    value: string[];
    onChange: (tags: string[]) => void;
    label?: string;
    helper?: string;
    error?: string;
    placeholder?: string;
    className?: string;
}

export default function TagInput({ value, onChange, label, helper, error, placeholder, className }: TagInputProps) {
    const [input, setInput] = useState('');

    const commit = (raw: string) => {
        const tag = raw.trim();
        if (!tag) return;
        const exists = value.some((t) => t.toLowerCase() === tag.toLowerCase());
        if (!exists) onChange([...value, tag]);
        setInput('');
    };

    const removeAt = (index: number) => {
        onChange(value.filter((_, i) => i !== index));
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === ',') {
            e.preventDefault();
            commit(input);
            return;
        }
        if ((e.key === 'Backspace' || e.key === 'Delete') && input === '' && value.length > 0) {
            e.preventDefault();
            removeAt(value.length - 1);
            return;
        }
        if (e.key.length === 1 && !ALLOWED_CHAR.test(e.key)) {
            e.preventDefault();
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const raw = e.target.value;
        if (raw.includes(',')) {
            const parts = raw.split(',');
            const last = parts.pop() ?? '';
            parts.forEach((p) => commit(p.replace(INVALID_CHARS, '')));
            setInput(last.replace(INVALID_CHARS, ''));
            return;
        }
        setInput(raw.replace(INVALID_CHARS, ''));
    };

    return (
        <div className="flex flex-col font-plus-jakarta-sans w-full">
            {label && <label className="mb-1 text-[16px] text-black">{label}</label>}
            <div className={`flex flex-wrap gap-2 items-center border rounded-[10px] border-line-color bg-transparent focus-within:border-blue-500 transition ${className || ''}`}>
                {value.map((tag, index) => (
                    <span key={`${tag}-${index}`} className="flex items-center gap-1 bg-gray-100 text-gray-800 rounded-full px-3 py-1 text-sm">
                        {tag}
                        <button
                            type="button"
                            onClick={() => removeAt(index)}
                            className="text-gray-500 hover:text-red-500 cursor-pointer leading-none"
                            aria-label={`Remove ${tag}`}
                        >
                            ×
                        </button>
                    </span>
                ))}
                <input
                    value={input}
                    onChange={handleChange}
                    onKeyDown={handleKeyDown}
                    onBlur={() => setInput((cur) => cur.replace(INVALID_CHARS, ''))}
                    placeholder={value.length === 0 ? placeholder : ''}
                    className="flex-1 min-w-[120px] bg-transparent focus:outline-none py-1"
                />
            </div>
            {helper && <span className="text-xs text-gray-500 mt-1">{helper}</span>}
            {error && <span className="text-xs text-red-500 mt-1">{error}</span>}
        </div>
    );
}
