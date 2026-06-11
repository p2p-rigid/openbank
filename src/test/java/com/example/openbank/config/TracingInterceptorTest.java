package com.example.openbank.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.example.openbank.L;
import com.example.openbank.R;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.integration.Slf4jThreadLocalAccessor;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

class TracingInterceptorTest {

  private final TracingInterceptor interceptor = new TracingInterceptor();

  @BeforeAll
  static void enableAutomaticContextPropagation() {
    ContextRegistry.getInstance()
        .registerThreadLocalAccessor(
            new Slf4jThreadLocalAccessor(
                L.Logging.TRACE_ID,
                L.Logging.PARENT_SPAN_ID,
                L.Logging.SPAN_ID,
                L.Logging.CLIENT_ID));
    Hooks.enableAutomaticContextPropagation();
  }

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  @Test
  void populatesMdcFromIncomingTraceHeadersAndClearsItAfterRequestCompletion() {
    var exchange =
        MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/hello")
                .header(R.Headers.TRACE_ID, "trace-from-client")
                .header(R.Headers.PARENT_SPAN_ID, "parent-from-client")
                .header(R.Headers.CLIENT_ID, "mobile-app")
                .build());

    WebFilterChain chain =
        currentExchange ->
            Mono.fromRunnable(
                () -> {
                  assertThat(MDC.get(L.Logging.TRACE_ID)).isEqualTo("trace-from-client");
                  assertThat(MDC.get(L.Logging.PARENT_SPAN_ID)).isEqualTo("parent-from-client");
                  assertThat(MDC.get(L.Logging.SPAN_ID)).isEqualTo(L.Logging.DEFAULT_SPAN_ID);
                  assertThat(MDC.get(L.Logging.CLIENT_ID)).isEqualTo("mobile-app");
                });

    StepVerifier.create(interceptor.filter(exchange, chain)).verifyComplete();

    assertThat(MDC.get(L.Logging.TRACE_ID)).isNull();
    assertThat(MDC.get(L.Logging.PARENT_SPAN_ID)).isNull();
    assertThat(MDC.get(L.Logging.SPAN_ID)).isNull();
    assertThat(MDC.get(L.Logging.CLIENT_ID)).isNull();
  }

  @Test
  void generatesTraceIdAndDefaultSpanFieldsWhenTraceHeadersAreMissing() {
    var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/hello").build());

    WebFilterChain chain =
        currentExchange ->
            Mono.fromRunnable(
                () -> {
                  String traceId = MDC.get(L.Logging.TRACE_ID);

                  assertThat(traceId).isNotBlank();
                  assertThatCode(() -> java.util.UUID.fromString(traceId))
                      .doesNotThrowAnyException();
                  assertThat(MDC.get(L.Logging.PARENT_SPAN_ID))
                      .isEqualTo(L.Logging.DEFAULT_SPAN_ID);
                  assertThat(MDC.get(L.Logging.SPAN_ID)).isEqualTo(L.Logging.DEFAULT_SPAN_ID);
                  assertThat(MDC.get(L.Logging.CLIENT_ID)).isNull();
                });

    StepVerifier.create(interceptor.filter(exchange, chain)).verifyComplete();

    assertThat(MDC.get(L.Logging.TRACE_ID)).isNull();
  }

  @Test
  void traceIdSurvivesReactiveSchedulerBoundaryWithAutomaticContextPropagation() {
    var exchange =
        MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/hello")
                .header(R.Headers.TRACE_ID, "trace-across-scheduler")
                .header(R.Headers.PARENT_SPAN_ID, "parent-across-scheduler")
                .header(R.Headers.CLIENT_ID, "client-across-scheduler")
                .build());
    AtomicReference<String> observedThread = new AtomicReference<>();

    WebFilterChain chain =
        currentExchange ->
            Mono.fromRunnable(
                    () -> {
                      observedThread.set(Thread.currentThread().getName());
                      assertThat(MDC.get(L.Logging.TRACE_ID)).isEqualTo("trace-across-scheduler");
                      assertThat(MDC.get(L.Logging.PARENT_SPAN_ID))
                          .isEqualTo("parent-across-scheduler");
                      assertThat(MDC.get(L.Logging.SPAN_ID)).isEqualTo(L.Logging.DEFAULT_SPAN_ID);
                      assertThat(MDC.get(L.Logging.CLIENT_ID)).isEqualTo("client-across-scheduler");
                    })
                .subscribeOn(Schedulers.boundedElastic())
                .then();

    StepVerifier.create(interceptor.filter(exchange, chain)).verifyComplete();

    assertThat(observedThread.get()).startsWith("boundedElastic-");
    assertThat(MDC.get(L.Logging.TRACE_ID)).isNull();
  }

  @Test
  void testString() {
    String a = "hello";
    String b = new String("hello");

    System.out.println(a == b);
    System.out.println(a.equals(b));
  }
}
