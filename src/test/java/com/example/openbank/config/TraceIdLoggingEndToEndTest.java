package com.example.openbank.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.openbank.L;
import com.example.openbank.R;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(OutputCaptureExtension.class)
class TraceIdLoggingEndToEndTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void requestTraceIdShowsUpInLoggedText(CapturedOutput output) {
    webTestClient
        .get()
        .uri("/api/hello")
        .header(R.Headers.TRACE_ID, "trace-e2e-123")
        .header(R.Headers.PARENT_SPAN_ID, "span-e2e-456")
        .header(R.Headers.CLIENT_ID, "client-e2e")
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON);

    assertThat(output.getOut())
        .contains("Handling greeting request")
        .contains("trace_id=trace-e2e-123")
        .contains("parent_span_id=span-e2e-456")
        .contains("span_id=" + L.Logging.DEFAULT_SPAN_ID);
  }
}
