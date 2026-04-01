'use client';
import { createContext, useContext, useState } from 'react';

interface UIContextType {
    isNavCollapsed: boolean;
    setNavCollapsed: React.Dispatch<React.SetStateAction<boolean>>;
}

const UIContext = createContext<UIContextType | undefined>(undefined);

export function UIProvider({ children }: { children: React.ReactNode }) {
    const [isNavCollapsed, setNavCollapsed] = useState<boolean>(false);

    return (
        <UIContext.Provider value={{ isNavCollapsed, setNavCollapsed }}>
            {children}
        </UIContext.Provider>
    );
}

export function useUI() {
    const context = useContext(UIContext);
    if (!context) {
        throw new Error('useUI Must be used in a UIProvider');
    }
    return context;
}