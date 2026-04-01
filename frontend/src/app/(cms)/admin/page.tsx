import NavigationBar from "@/app/(cms)/_components/NavigationBar";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: 'Dashboard',
    description: 'dashboard for managing the bookstore',
}

export default function DashboardPage() {
    return (
        <div className="flex">
            Dashboard here
        </div>
    )
}