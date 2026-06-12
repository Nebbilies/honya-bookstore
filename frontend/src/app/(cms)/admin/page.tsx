import {Metadata} from "next";
import {auth} from "@/auth";
import {BestSeller, DashboardSummary, MonthlyPoint, RecentOrder} from "@/types/types";
import StatCards from "@/app/(cms)/admin/_components/dashboard/StatCards";
import MonthlyChart from "@/app/(cms)/admin/_components/dashboard/MonthlyChart";
import BestSellersTable from "@/app/(cms)/admin/_components/dashboard/BestSellersTable";
import OrdersHistory from "@/app/(cms)/admin/_components/dashboard/OrdersHistory";

export const metadata: Metadata = {
    title: 'Dashboard',
    description: 'Analytics dashboard for managing the bookstore',
}

async function getJson<T>(path: string, token: string | undefined, fallback: T): Promise<T> {
    const apiBaseUrl = process.env.API_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    try {
        const res = await fetch(`${apiBaseUrl}${path}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                authorization: `Bearer ${token}`,
            },
            cache: 'no-store',
        });
        if (!res.ok) {
            return fallback;
        }
        return res.json();
    } catch {
        return fallback;
    }
}

export default async function DashboardPage() {
    const session = await auth();
    const token = session?.accessToken;
    const currentYear = new Date().getFullYear();
    const emptyMonths: MonthlyPoint[] = Array.from({length: 12}, (_, i) => ({month: i + 1, value: 0}));

    const [summary, sales, orders, bestSellers, recentOrders] = await Promise.all([
        getJson<DashboardSummary>('/dashboard/summary', token,
            {salesThisMonth: 0, totalUsers: 0, newCustomers: 0, ordersThisMonth: 0}),
        getJson<MonthlyPoint[]>(`/dashboard/sales-per-year?year=${currentYear}`, token, emptyMonths),
        getJson<MonthlyPoint[]>(`/dashboard/orders-per-year?year=${currentYear}`, token, emptyMonths),
        getJson<BestSeller[]>('/dashboard/best-sellers?period=YEAR', token, []),
        getJson<RecentOrder[]>('/dashboard/recent-orders', token, []),
    ]);

    return (
        <main className="flex flex-col gap-6">
            <h1 className="font-prata text-3xl">Dashboard</h1>
            <StatCards summary={summary}/>
            <div className="grid gap-6 xl:grid-cols-2">
                <MonthlyChart title="Sales Per Year" endpoint="sales-per-year" variant="line"
                              valueLabel="Revenue" currency initialData={sales} currentYear={currentYear}/>
                <MonthlyChart title="Orders Per Year" endpoint="orders-per-year" variant="bar"
                              valueLabel="Orders" initialData={orders} currentYear={currentYear}/>
            </div>
            <div className="grid gap-6 xl:grid-cols-3">
                <div className="xl:col-span-2">
                    <BestSellersTable initialData={bestSellers}/>
                </div>
                <OrdersHistory orders={recentOrders}/>
            </div>
        </main>
    );
}
