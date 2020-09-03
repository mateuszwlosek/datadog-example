package com.github.mateuszwlosek.datadog;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

	private static final String POD_NAME_ENVIRONMENT_VARIABLE = "POD_NAME";

	@Value("${spring.application.name}")
	private String applicationName;

	@Bean
	MeterRegistryCustomizer<MeterRegistry> metricsRegistryCustomizer() {
		final String podName = System.getenv(POD_NAME_ENVIRONMENT_VARIABLE);
		return registry -> registry.config().commonTags(applicationName, podName);
	}
}
