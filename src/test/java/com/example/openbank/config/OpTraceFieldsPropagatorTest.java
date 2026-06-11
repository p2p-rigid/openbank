package com.example.openbank.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.openbank.L;
import com.example.openbank.R;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OpTraceFieldsPropagatorTest {

  private static final TextMapGetter<Map<String, String>> GETTER =
      new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(Map<String, String> carrier) {
          return carrier.keySet();
        }

        @Override
        public String get(Map<String, String> carrier, String key) {
          return carrier.get(key);
        }
      };

  private static final TextMapSetter<Map<String, String>> SETTER =
      new TextMapSetter<>() {
        @Override
        public void set(Map<String, String> carrier, String key, String value) {
          carrier.put(key, value);
        }
      };

  private final OpTraceFieldsPropagator propagator = new OpTraceFieldsPropagator();

  @Test
  void fieldsReturnsTheIncomingCustomHeaderNames() {
    assertThat(propagator.fields())
        .containsExactlyInAnyOrder(
            R.Headers.TRACE_ID, R.Headers.PARENT_SPAN_ID, R.Headers.CLIENT_ID);
  }

  @Test
  void extractRenamesCustomHeadersToBaggageFieldsAndPreservesExistingBaggage() {
    Context context =
        Baggage.builder().put("existing", "kept").build().storeInContext(Context.root());
    Map<String, String> carrier =
        Map.of(
            R.Headers.TRACE_ID, "trace-from-client",
            R.Headers.PARENT_SPAN_ID, "parent-from-client",
            R.Headers.CLIENT_ID, "mobile-app");

    Context extracted = propagator.extract(context, carrier, GETTER);

    Baggage baggage = Baggage.fromContext(extracted);
    assertThat(baggage.getEntryValue(L.Logging.TRACE_ID)).isEqualTo("trace-from-client");
    assertThat(baggage.getEntryValue(L.Logging.PARENT_SPAN_ID)).isEqualTo("parent-from-client");
    assertThat(baggage.getEntryValue(L.Logging.CLIENT_ID)).isEqualTo("mobile-app");
    assertThat(baggage.getEntryValue("existing")).isEqualTo("kept");
  }

  @Test
  void extractHandlesNullContextAndNullGetterDefensively() {
    Context context =
        Baggage.builder().put("existing", "kept").build().storeInContext(Context.root());
    Map<String, String> emptyCarrier = Map.of();

    assertThat(propagator.extract(null, emptyCarrier, GETTER)).isNotNull();
    assertThat(propagator.extract(context, emptyCarrier, null)).isSameAs(context);
  }

  @Test
  void injectIsNoOp() {
    Context context =
        Baggage.builder()
            .put(L.Logging.TRACE_ID, "trace-from-client")
            .build()
            .storeInContext(Context.root());
    Map<String, String> carrier = new HashMap<>();

    propagator.inject(context, carrier, SETTER);

    assertThat(carrier).isEmpty();
  }
}
