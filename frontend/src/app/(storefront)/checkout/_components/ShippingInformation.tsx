'use client';

import ShortField from "@/components/Input Field/ShortField";
import {useEffect, useState} from "react";
import Button from "@/components/Button";
import Link from "next/link";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, Controller} from "react-hook-form";
import {useOrder} from "@/app/(storefront)/_context/OrderContext";
import {useSession} from "next-auth/react";

const shippingInfoSchema = z.object({
    email: z.string().email("Invalid email address"),
    firstName: z.string().min(1, "Required field"),
    lastName: z.string().min(1, "Required field"),
    address: z.string().min(1, "Required field"),
    phone: z.string().min(1, "Required field").regex(/^0\d{9,10}$/, "Invalid phone number"),
    city: z.string().min(1, "Required field"),
});

type shippingInfoValues = z.infer<typeof shippingInfoSchema>;

export default function ShippingInformation() {
    const orderContext = useOrder();
    const session = useSession();

    const form = useForm<shippingInfoValues>({
        resolver: zodResolver(shippingInfoSchema),
        defaultValues: {
            email: orderContext.orderInformation?.email || '',
            firstName: orderContext.orderInformation?.firstName || '',
            lastName: orderContext.orderInformation?.lastName || '',
            address: orderContext.orderInformation?.address || '',
            phone: orderContext.orderInformation?.phone ? orderContext.orderInformation.phone.replace('+84', '0') : '',
            city: orderContext.orderInformation?.city || '',
        },
        reValidateMode: "onChange",
    });

    const handleFormSubmit = (data: shippingInfoValues) => {
        orderContext.setOrderInformation({
            email: data.email,
            firstName: data.firstName,
            lastName: data.lastName,
            address: data.address,
            phone: data.phone.replace('0', '+84'),
            city: data.city,
            userId: session.data?.user?.id || '',
            returnUrl: `${window.location.origin}/checkout/payment/success`,
            provider: "COD",
        });
        window.location.href = '/checkout/payment';
    }

    return (
        <section className={'w-full flex flex-col items-start gap-6 bg-white px-[15px] py-[25px] rounded-lg border-1'}>
            <h4 className={'text-[26px] font-prata'}>Shipping Information</h4>
            <form id={'shipping-form'} onSubmit={form.handleSubmit(handleFormSubmit)}
                  className={'w-full flex flex-col items-start gap-2'}>
                <Controller
                    name="email"
                    control={form.control}
                    render={({field, fieldState}) => (
                        <ShortField
                            id={'email'}
                            label={'Email Address'}
                            type={'email'}
                            placeholder={'Enter your email address'}
                            onValueChange={field.onChange}
                            value={field.value}
                            error={fieldState.invalid ? fieldState.error?.message : undefined}
                            className={'w-full p-3 text-[18px]'}/>
                    )}/>
                <div className={'w-full flex items-start justify-between gap-4'}>
                    <Controller
                        name="firstName"
                        control={form.control}
                        render={({field, fieldState}) => (
                            <ShortField
                                id={'first-name'}
                                label={'First Name'}
                                type={'text'}
                                placeholder={'Enter your first name'}
                                onValueChange={field.onChange}
                                value={field.value}
                                error={fieldState.invalid ? fieldState.error?.message : undefined}
                                className={'w-full p-3 text-[18px]'}
                            />
                        )}
                    />
                    <Controller
                        name="lastName"
                        control={form.control}
                        render={({field, fieldState}) => (
                            <ShortField
                                id={'last-name'}
                                label={'Last Name'}
                                type={'text'}
                                placeholder={'Enter your last name'}
                                onValueChange={field.onChange}
                                value={field.value}
                                error={fieldState.invalid ? fieldState.error?.message : undefined}
                                className={'w-full p-3 text-[18px]'}
                            />
                        )}
                    />
                </div>
                <Controller
                    name="address"
                    control={form.control}
                    render={({field, fieldState}) => (
                        <ShortField
                            id={'address'}
                            label={'Address'}
                            type={'text'}
                            placeholder={'Enter your shipping address'}
                            onValueChange={field.onChange}
                            value={field.value}
                            error={fieldState.invalid ? fieldState.error?.message : undefined}
                            className={'w-full p-3 text-[18px]'}
                        />
                    )}
                />
                <div className={'w-full flex items-start justify-between gap-4'}>
                    <Controller
                        name="phone"
                        control={form.control}
                        render={({field, fieldState}) => (
                            <ShortField
                                id={'phone'}
                                label={'Phone Number'}
                                type={'text'}
                                placeholder={'Enter your phone number'}
                                onValueChange={field.onChange}
                                value={field.value}
                                error={fieldState.invalid ? fieldState.error?.message : undefined}
                                className={'w-full p-3 text-[18px]'}
                            />
                        )}
                    />
                    <Controller
                        name="city"
                        control={form.control}
                        render={({field, fieldState}) => (
                            <ShortField
                                id={'city'}
                                label={'City'}
                                type={'text'}
                                placeholder={'Enter your city'}
                                onValueChange={field.onChange}
                                value={field.value}
                                error={fieldState.invalid ? fieldState.error?.message : undefined}
                                className={'w-full p-3 text-[18px]'}
                            />
                        )}
                    />
                </div>
                <div className={'w-full flex items-start justify-between gap-4 mt-4'}>
                    <Link href={'/cart'} className={'mt-4 w-full'}>
                        <Button
                            shape={'rect'}
                            variant={'outline'}
                            className={'text-black hover:bg-gray-100 font-plus-jakarta-sans font-light text-[18px] w-full py-4'}>
                            Back
                        </Button>
                    </Link>
                    <div className={'mt-4 w-full'}>
                        <button
                            type={'submit'}
                            form={'shipping-form'}
                            disabled={form.formState.isSubmitting}
                            className={'bg-black text-white hover:bg-gray-700 font-plus-jakarta-sans font-light text-[18px] w-full py-4 rounded-[20px] cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed'}>
                            Proceed to Payment
                        </button>
                    </div>
                </div>
            </form>
        </section>
    )
}