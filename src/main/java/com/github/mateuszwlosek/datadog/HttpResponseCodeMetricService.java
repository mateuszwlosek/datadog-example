package com.github.mateuszwlosek.datadog;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class HttpResponseCodeMetricService implements WebMvcConfigurer {

	private static final String HTTP_RESPONSE_CODE_COUNTER_NAME = "http.response.code.%s";

	private final MeterRegistry meterRegistry;

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerInterceptorAdapter() {
			@Override
			public void afterCompletion(final HttpServletRequest request,
										final HttpServletResponse response,
										final Object handler,
										final Exception exception) {
				incrementMetricsForHttpResponseCode(response.getStatus());
			}
		});
	}

	private void incrementMetricsForHttpResponseCode(final int httpResponseCode) {
		final String counterName = String.format(HTTP_RESPONSE_CODE_COUNTER_NAME, httpResponseCode);
		meterRegistry.counter(counterName).increment();
	}
}