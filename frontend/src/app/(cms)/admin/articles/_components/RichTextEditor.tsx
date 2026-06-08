'use client';

import { useEditor, EditorContent, Editor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import Image from '@tiptap/extension-image';
import TextAlign from '@tiptap/extension-text-align';
import { useEffect, useState } from 'react';
import Icon, { IconName } from '@/components/Icon';
import SelectMediaDialog from '@/app/(cms)/admin/media/upload/_components/SelectMediaDialog';

interface RichTextEditorProps {
    value: string;
    onChange: (html: string) => void;
    error?: string;
}

const headingOptions = [
    { label: 'Paragraph', value: 'paragraph' },
    { label: 'Heading 1', value: '1' },
    { label: 'Heading 2', value: '2' },
    { label: 'Heading 3', value: '3' },
];

function ToolbarButton({ icon, active, disabled, onClick }: { icon: IconName; active?: boolean; disabled?: boolean; onClick: () => void }) {
    return (
        <button
            type="button"
            disabled={disabled}
            onClick={onClick}
            className={`p-1.5 rounded-md transition-colors ${active ? 'bg-gray-200 text-black' : 'text-gray-700 hover:bg-gray-100'} disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer`}
        >
            <Icon name={icon} size={20} />
        </button>
    );
}

function Toolbar({ editor, onAddMedia }: { editor: Editor; onAddMedia: () => void }) {
    const currentHeading = editor.isActive('heading', { level: 1 })
        ? '1'
        : editor.isActive('heading', { level: 2 })
            ? '2'
            : editor.isActive('heading', { level: 3 })
                ? '3'
                : 'paragraph';

    const onHeadingChange = (value: string) => {
        if (value === 'paragraph') {
            editor.chain().focus().setParagraph().run();
        } else {
            editor.chain().focus().toggleHeading({ level: Number(value) as 1 | 2 | 3 }).run();
        }
    };

    const onSetLink = () => {
        const previous = editor.getAttributes('link').href as string | undefined;
        const url = window.prompt('Enter URL', previous || 'https://');
        if (url === null) return;
        if (url === '') {
            editor.chain().focus().extendMarkRange('link').unsetLink().run();
            return;
        }
        editor.chain().focus().extendMarkRange('link').setLink({ href: url }).run();
    };

    return (
        <div className="flex flex-wrap items-center gap-1 border-b border-line-color p-2">
            <select
                value={currentHeading}
                onChange={(e) => onHeadingChange(e.target.value)}
                className="border border-line-color rounded-md px-2 py-1 text-sm font-plus-jakarta-sans focus:outline-none focus:border-button-blue cursor-pointer"
            >
                {headingOptions.map((opt) => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                ))}
            </select>
            <ToolbarButton icon="bold" active={editor.isActive('bold')} onClick={() => editor.chain().focus().toggleBold().run()} />
            <ToolbarButton icon="italic" active={editor.isActive('italic')} onClick={() => editor.chain().focus().toggleItalic().run()} />
            <ToolbarButton icon="underline" active={editor.isActive('underline')} onClick={() => editor.chain().focus().toggleUnderline().run()} />
            <ToolbarButton icon="strikethrough" active={editor.isActive('strike')} onClick={() => editor.chain().focus().toggleStrike().run()} />
            <ToolbarButton icon="list-bulleted" active={editor.isActive('bulletList')} onClick={() => editor.chain().focus().toggleBulletList().run()} />
            <ToolbarButton icon="list-numbered" active={editor.isActive('orderedList')} onClick={() => editor.chain().focus().toggleOrderedList().run()} />
            <ToolbarButton icon="quote" active={editor.isActive('blockquote')} onClick={() => editor.chain().focus().toggleBlockquote().run()} />
            <ToolbarButton icon="code" active={editor.isActive('codeBlock')} onClick={() => editor.chain().focus().toggleCodeBlock().run()} />
            <ToolbarButton icon="link" active={editor.isActive('link')} onClick={onSetLink} />
            <ToolbarButton icon="align-left" active={editor.isActive({ textAlign: 'left' })} onClick={() => editor.chain().focus().setTextAlign('left').run()} />
            <ToolbarButton icon="align-center" active={editor.isActive({ textAlign: 'center' })} onClick={() => editor.chain().focus().setTextAlign('center').run()} />
            <ToolbarButton icon="align-right" active={editor.isActive({ textAlign: 'right' })} onClick={() => editor.chain().focus().setTextAlign('right').run()} />
            <ToolbarButton icon="horizontal-rule" onClick={() => editor.chain().focus().setHorizontalRule().run()} />
            <ToolbarButton icon="undo" disabled={!editor.can().undo()} onClick={() => editor.chain().focus().undo().run()} />
            <ToolbarButton icon="redo" disabled={!editor.can().redo()} onClick={() => editor.chain().focus().redo().run()} />
            <button
                type="button"
                onClick={onAddMedia}
                className="flex items-center gap-1.5 ml-auto border border-line-color rounded-md px-3 py-1.5 text-sm text-gray-700 hover:bg-gray-100 transition-colors cursor-pointer"
            >
                <Icon name="image" size={18} />
                Add Media
            </button>
        </div>
    );
}

export default function RichTextEditor({ value, onChange, error }: RichTextEditorProps) {
    const [isMediaDialogOpen, setIsMediaDialogOpen] = useState(false);

    const editor = useEditor({
        immediatelyRender: false,
        extensions: [
            StarterKit.configure({
                link: { openOnClick: false },
            }),
            Image,
            TextAlign.configure({ types: ['heading', 'paragraph'] }),
        ],
        content: value,
        editorProps: {
            attributes: {
                class: 'prose max-w-none min-h-[400px] max-h-[500px] overflow-y-auto p-4 focus:outline-none',
            },
        },
        onUpdate: ({ editor }) => {
            onChange(editor.getHTML());
        },
    });

    useEffect(() => {
        if (editor && value !== editor.getHTML()) {
            editor.commands.setContent(value, { emitUpdate: false });
        }
    }, [editor, value]);

    if (!editor) return null;

    return (
        <div className="flex flex-col w-full">
            <div className={`border rounded-[10px] overflow-hidden ${error ? 'border-red-500' : 'border-line-color'}`}>
                <Toolbar editor={editor} onAddMedia={() => setIsMediaDialogOpen(true)} />
                <EditorContent editor={editor} />
            </div>
            {error && <span className="text-xs text-red-500 mt-1">{error}</span>}
            {isMediaDialogOpen && (
                <SelectMediaDialog
                    isOpen={isMediaDialogOpen}
                    onClose={() => setIsMediaDialogOpen(false)}
                    imageType="cover"
                    appendImage={(img) => {
                        editor.chain().focus().setImage({ src: img.url }).run();
                        setIsMediaDialogOpen(false);
                    }}
                    removeImage={() => {}}
                />
            )}
        </div>
    );
}
