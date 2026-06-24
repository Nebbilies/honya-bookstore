package com.honya.platform.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class HonyaObservability {

    @Bean
    public OtelLogbackInstaller otelLogbackInstaller(ObjectProvider<OpenTelemetry> openTelemetry) {
        return new OtelLogbackInstaller(openTelemetry);
    }

    static class OtelLogbackInstaller {
        private final ObjectProvider<OpenTelemetry> openTelemetry;

        OtelLogbackInstaller(ObjectProvider<OpenTelemetry> openTelemetry) {
            this.openTelemetry = openTelemetry;
        }

        @EventListener(ApplicationReadyEvent.class)
        void install() {
            OpenTelemetry otel = openTelemetry.getIfAvailable();
            if (otel != null) {
                OpenTelemetryAppender.install(otel);
            }
        }
    }
}
