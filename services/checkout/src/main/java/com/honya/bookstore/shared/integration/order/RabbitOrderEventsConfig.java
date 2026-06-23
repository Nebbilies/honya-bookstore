package com.honya.bookstore.shared.integration.order;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitOrderEventsConfig {

    public static final String CHECKOUT_PAYMENT_QUEUE = "checkout.payment.events";
    public static final String CHECKOUT_PAYMENT_RETRIED_QUEUE = "checkout.payment.retried";

    @Bean
    DirectExchange orderEventsExchange() {
        return new DirectExchange(OrderEventsTopology.EXCHANGE, true, false);
    }

    @Bean
    Queue checkoutPaymentEventsQueue() {
        return new Queue(CHECKOUT_PAYMENT_QUEUE, true);
    }

    @Bean
    Binding paymentConfirmedBinding(Queue checkoutPaymentEventsQueue, DirectExchange orderEventsExchange) {
        return BindingBuilder.bind(checkoutPaymentEventsQueue).to(orderEventsExchange).with(OrderEventsTopology.PAYMENT_CONFIRMED);
    }

    @Bean
    Queue checkoutPaymentRetriedQueue() {
        return new Queue(CHECKOUT_PAYMENT_RETRIED_QUEUE, true);
    }

    @Bean
    Binding paymentRetriedBinding(Queue checkoutPaymentRetriedQueue, DirectExchange orderEventsExchange) {
        return BindingBuilder.bind(checkoutPaymentRetriedQueue).to(orderEventsExchange).with(OrderEventsTopology.PAYMENT_RETRIED);
    }
}
