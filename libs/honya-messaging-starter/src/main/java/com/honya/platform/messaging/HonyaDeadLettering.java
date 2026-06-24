package com.honya.platform.messaging;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@AutoConfiguration(beforeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@ConditionalOnClass(RabbitTemplate.class)
public class HonyaDeadLettering {

    @Bean
    public MessageRecoverer messageRecoverer(
            RabbitTemplate rabbitTemplate,
            @Value("${bookstore.messaging.dead-letter.exchange}") String deadLetterExchange) {
        return new RepublishMessageRecoverer(rabbitTemplate, deadLetterExchange);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageRecoverer messageRecoverer,
            @Value("${bookstore.messaging.retry.max-retries:2}") long maxRetries,
            @Value("${bookstore.messaging.retry.initial-interval-ms:1000}") long initialIntervalMs,
            @Value("${bookstore.messaging.retry.multiplier:2.0}") double multiplier,
            @Value("${bookstore.messaging.retry.max-interval-ms:10000}") long maxIntervalMs) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .configureRetryPolicy(policy -> policy
                        .maxRetries(maxRetries)
                        .delay(Duration.ofMillis(initialIntervalMs))
                        .multiplier(multiplier)
                        .maxDelay(Duration.ofMillis(maxIntervalMs)))
                .recoverer(messageRecoverer)
                .build());
        return factory;
    }

    @Bean
    public Declarables deadLetterTopology(
            @Value("${bookstore.messaging.dead-letter.exchange}") String deadLetterExchange,
            @Value("${bookstore.messaging.dead-letter.queue}") String deadLetterQueue) {
        return DeadLetterSupport.deadLetterTopology(deadLetterExchange, deadLetterQueue);
    }
}
