package com.honya.platform.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class HonyaObservability {

    @Bean
    public OtelLogbackInstaller otelLogbackInstaller(ObjectProvider<OpenTelemetry> openTelemetry,
                                                     @Value("${otel.enabled:false}") boolean otelEnabled) {
        return new OtelLogbackInstaller(openTelemetry, otelEnabled);
    }

    static class OtelLogbackInstaller {
        private final ObjectProvider<OpenTelemetry> openTelemetry;
        private final boolean otelEnabled;

        OtelLogbackInstaller(ObjectProvider<OpenTelemetry> openTelemetry, boolean otelEnabled) {
            this.openTelemetry = openTelemetry;
            this.otelEnabled = otelEnabled;
        }

        @EventListener(ApplicationReadyEvent.class)
        void install() {
            if (!otelEnabled) {
                return;
            }
            OpenTelemetry otel = openTelemetry.getIfAvailable();
            if (otel != null) {
                OpenTelemetryAppender.install(otel);
            }
        }
    }
}
