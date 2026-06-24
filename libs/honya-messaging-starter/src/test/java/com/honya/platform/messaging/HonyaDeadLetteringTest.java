package com.honya.platform.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HonyaDeadLetteringTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HonyaDeadLettering.class))
            .withBean(ConnectionFactory.class, () -> mock(ConnectionFactory.class))
            .withBean(RabbitTemplate.class, () -> mock(RabbitTemplate.class))
            .withPropertyValues(
                    "bookstore.messaging.dead-letter.exchange=catalog.dlx",
                    "bookstore.messaging.dead-letter.queue=catalog.dlq");

    @Test
    void contextProvidesDeadLetterBeans() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(SimpleRabbitListenerContainerFactory.class);
            assertThat(context).hasBean("messageRecoverer");
            assertThat(context).hasBean("deadLetterTopology");
            assertThat(context.getBean(MessageRecoverer.class)).isInstanceOf(RepublishMessageRecoverer.class);
            assertThat(context.getBean(Declarables.class).getDeclarables()).hasSize(3);
        });
    }

    @Test
    void listenerFactoryDoesNotRequeueRejectedMessages() {
        runner.run(context -> {
            SimpleRabbitListenerContainerFactory factory = context.getBean(SimpleRabbitListenerContainerFactory.class);
            assertThat(ReflectionTestUtils.getField(factory, "defaultRequeueRejected")).isEqualTo(false);
        });
    }

    @Test
    void ourListenerFactoryWinsOverBootDefaultWhenRabbitAutoConfigPresent() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RabbitAutoConfiguration.class, HonyaDeadLettering.class))
                .withPropertyValues(
                        "bookstore.messaging.dead-letter.exchange=catalog.dlx",
                        "bookstore.messaging.dead-letter.queue=catalog.dlq")
                .run(context -> {
                    assertThat(context.getBeanNamesForType(SimpleRabbitListenerContainerFactory.class)).hasSize(1);
                    SimpleRabbitListenerContainerFactory factory =
                            context.getBean("rabbitListenerContainerFactory", SimpleRabbitListenerContainerFactory.class);
                    assertThat(ReflectionTestUtils.getField(factory, "defaultRequeueRejected")).isEqualTo(false);
                });
    }
}
