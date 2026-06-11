package com.example.openbank.config;

import com.example.openbank.L;
import com.example.openbank.R;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.BaggageEntryMetadata;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OpTraceFieldsPropagator implements TextMapPropagator {

  private static final BaggageEntryMetadata PROPAGATION_UNLIMITED =
      BaggageEntryMetadata.create("propagation=unlimited");

  private static final Map<String, String> HEADER_TO_BAGGAGE_FIELD =
      Map.of(
          R.Headers.CLIENT_ID,
          L.Logging.CLIENT_ID,
          R.Headers.TRACE_ID,
          L.Logging.TRACE_ID,
          R.Headers.PARENT_SPAN_ID,
          L.Logging.PARENT_SPAN_ID);

  @Override
  public Collection<String> fields() {
    return List.of(R.Headers.TRACE_ID, R.Headers.PARENT_SPAN_ID, R.Headers.CLIENT_ID);
  }

  @Override
  public <C> void inject(Context context, C carrier, TextMapSetter<C> setter) {
    // Outgoing W3C and baggage header serialization is handled by Spring Boot.
  }

  @Override
  public <C> Context extract(Context context, C carrier, TextMapGetter<C> getter) {
    if (context == null) {
      context = Context.root();
    }
    if (getter == null) {
      return context;
    }

    var builder = Baggage.current().toBuilder();
    Baggage.fromContext(context)
        .asMap()
        .forEach((key, entry) -> builder.put(key, entry.getValue(), entry.getMetadata()));

    HEADER_TO_BAGGAGE_FIELD.forEach(
        (headerName, baggageField) -> {
          String value = getter.get(carrier, headerName);
          if (value != null) {
            builder.put(baggageField, value, PROPAGATION_UNLIMITED);
          }
        });

    return context.with(builder.build());
  }
}
