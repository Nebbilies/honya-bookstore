'use client';

import Icon from "@/components/Icon";
import {useRouter} from "next/navigation";
import {toast} from "sonner";
import {useSession} from "next-auth/react";
import {Book} from "@/types/types";
import {useConfirmation} from "@/app/(cms)/_context/AlertDialogContext";
import {Controller, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import ShortField from "@/components/Input Field/ShortField";
import {Dialog, DialogContent, DialogDescription, DialogHeader, DialogTrigger, DialogTitle} from "@/components/ui/dialog";
import {useState} from "react";

interface BookTableActionsProps {
    book: Book
}

const AddStockDialog = ({ book }: { book: Book }) => {
    const [open, setOpen] = useState<boolean>(false);
    const onOpenChange = (isOpen: boolean) => {
        setOpen(isOpen);
    }

    const session = useSession();
    const router = useRouter();

    const form = useForm<{ quantity: number }>({
        resolver: zodResolver(z.object({
            quantity: z.coerce.number<number>("Invalid number").min(1, "Quantity must be at least 1"),
        })),
        defaultValues: {
            quantity: 1
        }
    });

    const handleSubmit = async (data: { quantity: number }) => {
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/books/${book.id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session.data?.accessToken}`,
                },
                body: JSON.stringify({
                    book,
                    stockQuantity: book.stockQuantity + data.quantity
                }),
            });
            if (!res.ok) {
                const err = await res.json();
                toast.error(err.message || "Failed to add stock");
                throw new Error('Failed to add stock');
            }
            toast.success("Stock added successfully");
            router.refresh();
            onOpenChange(false);
            form.reset();
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogTrigger asChild>
                <button className="text-black hover:text-blue-400 cursor-pointer">
                    <Icon name={'add'} size={25}/>
                </button>
            </DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>
                        Add Stock
                    </DialogTitle>
                    <DialogDescription>
                        Add stock for &#34;{book.title}&#34;
                    </DialogDescription>
                </DialogHeader>
                <form onSubmit={form.handleSubmit(handleSubmit)}>
                    <Controller
                        control={form.control}
                        name="quantity"
                        render={({field, fieldState}) => (
                            <div className="flex flex-col gap-4">
                                <ShortField
                                    className={'h-10 p-3'}
                                    type={'number'}
                                    value={field.value}
                                    onValueChange={(e) => field.onChange(e.target.value)}
                                    error={fieldState.error?.message}
                                />
                            </div>
                        )}/>
                    <div className="mt-6 flex justify-end">
                        <button
                            type="submit"
                            className="bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
                        >
                            Add Stock
                        </button>
                    </div>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default function BookTableActions({book}: BookTableActionsProps) {
    const router = useRouter();
    const session = useSession();
    const { openConfirmation } = useConfirmation();
    const bookId = book.id;

    const handleEdit = () => {
        router.push('/admin/books/edit?id=' + bookId);
    }

    const handleDelete = async () => {
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/books/${bookId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    authorization: `Bearer ${session.data?.accessToken}`,
                },
            });

            if (!res.ok) {
                const err = await res.json();
                toast.error(err.message || "Failed to delete book");
                throw new Error('Failed to delete book');
            }

            toast.success("Book deleted successfully");
            router.refresh();
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <td className="px-4 py-3 text-center gap-3">
            <AddStockDialog book={book} />
            <button onClick={handleEdit} className="text-gray-600 hover:text-blue-400 ml-3 cursor-pointer">
                <Icon name={'edit'} size={25}/>
            </button>
            <button onClick={() => openConfirmation({
                title: 'Delete Book',
                description: `Are you sure you want to delete the book "${book.title}"? This action cannot be undone.`,
                onAction: handleDelete
            })} className="text-red-500 hover:text-red-700 ml-3 cursor-pointer">
                <Icon name={'trash'} size={25}/>
            </button>
        </td>
    );
}