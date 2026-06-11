package com.example.openbank.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;

class TracingConfigurationTest {

  private final ReactiveWebApplicationContextRunner contextRunner =
      new ReactiveWebApplicationContextRunner().withUserConfiguration(TracingConfiguration.class);

  @Test
  void tracingInterceptorIsRegisteredByDefault() {
    contextRunner.run(context -> assertThat(context).hasSingleBean(TracingInterceptor.class));
  }

  @Test
  void tracingInterceptorCanBeDisabledByProperty() {
    contextRunner
        .withPropertyValues("tracing-interceptor.enabled=false")
        .run(context -> assertThat(context).doesNotHaveBean(TracingInterceptor.class));
  }
}
