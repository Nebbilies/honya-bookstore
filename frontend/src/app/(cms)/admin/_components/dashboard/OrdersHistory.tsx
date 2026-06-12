import Link from "next/link";
import {RecentOrder} from "@/types/types";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";

export default function OrdersHistory({orders}: { orders: RecentOrder[] }) {
    return (
        <Card className="h-full">
            <CardHeader>
                <CardTitle className="font-prata text-xl">Orders History</CardTitle>
                <p className="text-sm text-muted-foreground">Showing {orders.length} latest orders</p>
            </CardHeader>
            <CardContent className="flex flex-col gap-4">
                {orders.length === 0 && (
                    <p className="text-sm text-muted-foreground">No recent orders.</p>
                )}
                {orders.map((order) => (
                    <Link key={order.id} href={`/admin/orders/${order.id}`} className="group flex items-start gap-3">
                        <span className="mt-1.5 size-2 shrink-0 rounded-full bg-blue-500"/>
                        <div className="flex flex-col">
                            <span className="text-sm font-medium group-hover:underline">
                                New Order #{order.id.slice(0, 8)}
                            </span>
                            <span className="text-xs text-muted-foreground">
                                {new Date(order.createdAt).toLocaleString("vi-VN")}
                            </span>
                        </div>
                    </Link>
                ))}
            </CardContent>
        </Card>
    );
}
