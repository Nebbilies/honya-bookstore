"use client";

import {useState} from "react";
import useSWR from "swr";
import {useSession} from "next-auth/react";
import {fetcher} from "@/lib/utils";
import {BestSeller} from "@/types/types";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Tabs, TabsList, TabsTrigger} from "@/components/ui/tabs";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";

type Period = "WEEK" | "MONTH" | "YEAR";

export default function BestSellersTable({initialData}: { initialData: BestSeller[] }) {
    const {data: session} = useSession();
    const token = session?.accessToken;
    const [period, setPeriod] = useState<Period>("YEAR");

    const key = token ? `${process.env.NEXT_PUBLIC_API_URL}/dashboard/best-sellers?period=${period}` : null;
    const {data} = useSWR<BestSeller[]>(key, (url: string) => fetcher(url, token ?? ""), {
        fallbackData: period === "YEAR" ? initialData : undefined,
    });
    const rows = data ?? [];

    return (
        <Card className="h-full">
            <CardHeader className="flex flex-row items-start justify-between">
                <div className="flex flex-col gap-1">
                    <CardTitle className="font-prata text-xl">Best Sellers</CardTitle>
                    <p className="text-sm text-muted-foreground">Based on {period.toLowerCase()}</p>
                </div>
                <Tabs value={period} onValueChange={(value) => setPeriod(value as Period)}>
                    <TabsList>
                        <TabsTrigger value="WEEK">Week</TabsTrigger>
                        <TabsTrigger value="MONTH">Month</TabsTrigger>
                        <TabsTrigger value="YEAR">Year</TabsTrigger>
                    </TabsList>
                </Tabs>
            </CardHeader>
            <CardContent>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Title</TableHead>
                            <TableHead>Author</TableHead>
                            <TableHead className="text-right">Total sold</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {rows.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={3} className="py-8 text-center text-muted-foreground">
                                    No sales in this period.
                                </TableCell>
                            </TableRow>
                        )}
                        {rows.map((row, index) => (
                            <TableRow key={index}>
                                <TableCell className="font-medium">{row.title}</TableCell>
                                <TableCell>{row.author}</TableCell>
                                <TableCell className="text-right">{row.totalSold}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </CardContent>
        </Card>
    );
}
