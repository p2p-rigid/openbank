package com.example.openbank.config;

import com.example.openbank.L;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.integration.Slf4jThreadLocalAccessor;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
public class TracingConfiguration {

  @PostConstruct
  void enableReactorMdcPropagation() {
    ContextRegistry.getInstance()
        .registerThreadLocalAccessor(
            new Slf4jThreadLocalAccessor(
                L.Logging.TRACE_ID,
                L.Logging.PARENT_SPAN_ID,
                L.Logging.SPAN_ID,
                L.Logging.CLIENT_ID));
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "tracing-interceptor",
      name = "enabled",
      havingValue = "true",
      matchIfMissing = true)
  TracingInterceptor tracingInterceptor() {
    return new TracingInterceptor();
  }
}
