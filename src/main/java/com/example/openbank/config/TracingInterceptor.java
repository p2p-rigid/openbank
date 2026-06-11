package com.example.openbank.config;

import com.example.openbank.L;
import com.example.openbank.R;
import io.micrometer.context.integration.Slf4jThreadLocalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@Order(1)
public class TracingInterceptor implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    Map<String, String> mdcFields = mdcFields(exchange.getRequest().getHeaders());

    return Mono.defer(
            () -> {
              if (!Hooks.isAutomaticContextPropagationEnabled()) {
                putMdcFields(mdcFields);
              }
              return chain
                  .filter(exchange)
                  .contextWrite(context -> context.put(Slf4jThreadLocalAccessor.KEY, mdcFields));
            })
        .doFinally(signal -> MDC.clear());
  }

  private Map<String, String> mdcFields(HttpHeaders headers) {
    Map<String, String> fields = new HashMap<>();
    fields.put(
        L.Logging.TRACE_ID,
        firstHeaderOrDefault(headers, R.Headers.TRACE_ID, UUID.randomUUID().toString()));
    fields.put(
        L.Logging.PARENT_SPAN_ID,
        firstHeaderOrDefault(headers, R.Headers.PARENT_SPAN_ID, L.Logging.DEFAULT_SPAN_ID));
    fields.put(L.Logging.SPAN_ID, L.Logging.DEFAULT_SPAN_ID);

    String clientId = headers.getFirst(R.Headers.CLIENT_ID);
    if (clientId != null) {
      fields.put(L.Logging.CLIENT_ID, clientId);
    }
    return fields;
  }

  private String firstHeaderOrDefault(HttpHeaders headers, String headerName, String defaultValue) {
    String headerValue = headers.getFirst(headerName);
    return headerValue != null ? headerValue : defaultValue;
  }

  private void putMdcFields(Map<String, String> fields) {
    fields.forEach(MDC::put);
  }
}
