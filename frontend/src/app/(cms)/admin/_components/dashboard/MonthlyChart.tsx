"use client";

import {useState} from "react";
import useSWR from "swr";
import {useSession} from "next-auth/react";
import {Bar, BarChart, CartesianGrid, Line, LineChart, XAxis, YAxis} from "recharts";
import {fetcher} from "@/lib/utils";
import {MonthlyPoint} from "@/types/types";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent} from "@/components/ui/chart";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";

const MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

interface MonthlyChartProps {
    title: string;
    endpoint: string;
    variant: "line" | "bar";
    valueLabel: string;
    currency?: boolean;
    initialData: MonthlyPoint[];
    currentYear: number;
}

export default function MonthlyChart(
    {title, endpoint, variant, valueLabel, currency, initialData, currentYear}: MonthlyChartProps) {
    const {data: session} = useSession();
    const token = session?.accessToken;
    const [year, setYear] = useState(currentYear);
    const years = Array.from({length: 5}, (_, i) => currentYear - i);

    const key = token ? `${process.env.NEXT_PUBLIC_API_URL}/dashboard/${endpoint}?year=${year}` : null;
    const {data} = useSWR<MonthlyPoint[]>(key, (url: string) => fetcher(url, token ?? ""), {
        fallbackData: year === currentYear ? initialData : undefined,
    });

    const chartData = (data ?? []).map((point) => ({
        month: MONTHS[point.month - 1] ?? String(point.month),
        value: point.value,
    }));

    const config = {value: {label: valueLabel, color: "var(--chart-1)"}} satisfies ChartConfig;
    const formatAxis = (value: number) =>
        currency ? value.toLocaleString("vi-VN", {notation: "compact"}) : String(value);

    return (
        <Card>
            <CardHeader>
                <CardTitle className="font-prata text-xl">{title}</CardTitle>
            </CardHeader>
            <CardContent className="flex flex-col gap-4">
                <ChartContainer config={config} className="h-[240px] w-full">
                    {variant === "line" ? (
                        <LineChart data={chartData} margin={{left: 12, right: 12}}>
                            <CartesianGrid vertical={false}/>
                            <XAxis dataKey="month" tickLine={false} axisLine={false} tickMargin={8}/>
                            <YAxis tickLine={false} axisLine={false} width={44} tickFormatter={formatAxis}/>
                            <ChartTooltip content={<ChartTooltipContent/>}/>
                            <Line dataKey="value" type="monotone" stroke="var(--color-value)" strokeWidth={2}
                                  dot={false}/>
                        </LineChart>
                    ) : (
                        <BarChart data={chartData} margin={{left: 12, right: 12}}>
                            <CartesianGrid vertical={false}/>
                            <XAxis dataKey="month" tickLine={false} axisLine={false} tickMargin={8}/>
                            <YAxis tickLine={false} axisLine={false} width={44} tickFormatter={formatAxis}/>
                            <ChartTooltip content={<ChartTooltipContent/>}/>
                            <Bar dataKey="value" fill="var(--color-value)" radius={4}/>
                        </BarChart>
                    )}
                </ChartContainer>
                <div className="flex items-center gap-2">
                    <span className="text-sm text-muted-foreground">Year:</span>
                    <Select value={String(year)} onValueChange={(value) => setYear(Number(value))}>
                        <SelectTrigger className="w-32">
                            <SelectValue/>
                        </SelectTrigger>
                        <SelectContent>
                            {years.map((option) => (
                                <SelectItem key={option} value={String(option)}>{option}</SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                </div>
            </CardContent>
        </Card>
    );
}
