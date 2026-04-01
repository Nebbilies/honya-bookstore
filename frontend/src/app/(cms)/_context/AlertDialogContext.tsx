"use client"; // Nếu bạn dùng Next.js App Router

import React, { createContext, useContext, useState, ReactNode, useCallback } from "react";
import {
    AlertDialog, AlertDialogAction, AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription, AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle
} from "@/components/ui/alert-dialog";

interface ConfirmationData {
    title?: string;
    description?: string;
    cancelLabel?: string;
    actionLabel?: string;
    onAction?: () => void;
    onCancel?: () => void;
}

interface ConfirmationState extends ConfirmationData {
    open: boolean;
}

interface ConfirmationContextType extends ConfirmationState {
    openConfirmation: (data: ConfirmationData) => void;
    closeConfirmation: () => void;
}

const initialState: ConfirmationState = {
    open: false,
    title: "",
    description: "",
    cancelLabel: "",
    actionLabel: "",
    onAction: () => {},
    onCancel: () => {},
};

const ConfirmationContext = createContext<ConfirmationContextType | undefined>(undefined);

export const ConfirmationProvider = ({ children }: { children: ReactNode }) => {
    const [state, setState] = useState<ConfirmationState>(initialState);

    const openConfirmation = useCallback((data: ConfirmationData) => {
        setState({
            open: true,
            ...data,
        });
    }, []);

    const closeConfirmation = useCallback(() => {
        setState(initialState);
    }, []);

    return (
        <ConfirmationContext.Provider
            value={{
                ...state,
                openConfirmation,
                closeConfirmation,
            }}
        >
            {children}
            <AlertDialog open={state.open} onOpenChange={closeConfirmation}>
                <AlertDialogContent className={'font-plus-jakarta-sans'}>
                    <AlertDialogHeader>
                        <AlertDialogTitle>{state.title || "Are you sure?"}</AlertDialogTitle>
                        <AlertDialogDescription>
                            {state.description || "This action cannot be undone."}
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>{state.cancelLabel || "Cancel"}</AlertDialogCancel>
                        <AlertDialogAction className={'bg-red-500 hover:bg-red-600'} onClick={state.onAction}>{state.actionLabel || "Confirm"}</AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </ConfirmationContext.Provider>
    );
};

// 4. Tạo Hook custom để sử dụng dễ dàng
export const useConfirmation = () => {
    const context = useContext(ConfirmationContext);
    if (!context) {
        throw new Error("useConfirmation must be used within a ConfirmationProvider");
    }
    return context;
};