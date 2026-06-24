package com.honya.platform.observability;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class HonyaObservabilityTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(HonyaObservability.class);

    @Test
    void registersLogbackInstallerBean() {
        runner.run(context -> assertThat(context).hasSingleBean(HonyaObservability.OtelLogbackInstaller.class));
    }

    @Test
    void startsCleanlyWithNoOpenTelemetryBeanPresent() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBeansOfType(io.opentelemetry.api.OpenTelemetry.class)).isEmpty();
        });
    }
}
