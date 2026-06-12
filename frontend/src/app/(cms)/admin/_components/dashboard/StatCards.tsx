import {Card} from "@/components/ui/card";
import {DollarSign, ShoppingCart, UserPlus, Users} from "lucide-react";
import {DashboardSummary} from "@/types/types";

const formatVND = (value: number) =>
    value.toLocaleString("vi-VN", {style: "currency", currency: "VND", maximumFractionDigits: 0});

const formatCount = (value: number) => value.toLocaleString("vi-VN");

export default function StatCards({summary}: { summary: DashboardSummary }) {
    const cards = [
        {label: "Sales This Month", value: formatVND(summary.salesThisMonth), icon: DollarSign},
        {label: "Total Users", value: formatCount(summary.totalUsers), icon: Users},
        {label: "New Customers", value: formatCount(summary.newCustomers), icon: UserPlus},
        {label: "Orders This Month", value: formatCount(summary.ordersThisMonth), icon: ShoppingCart},
    ];

    return (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {cards.map((card) => (
                <Card key={card.label} className="flex-row items-center justify-between gap-4 p-5">
                    <div className="flex flex-col gap-1">
                        <span className="text-sm text-muted-foreground">{card.label}</span>
                        <span className="text-2xl font-semibold">{card.value}</span>
                    </div>
                    <div className="rounded-xl bg-blue-500 p-3 text-white">
                        <card.icon className="size-5"/>
                    </div>
                </Card>
            ))}
        </div>
    );
}
