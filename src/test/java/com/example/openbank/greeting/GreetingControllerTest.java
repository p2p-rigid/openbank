package com.example.openbank.greeting;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(GreetingController.class)
@Import(GreetingControllerTest.FixedClockConfiguration.class)
class GreetingControllerTest {

    private final WebTestClient webTestClient;

    @Autowired
    GreetingControllerTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    void helloReturnsImmutableGreeting() {
        webTestClient.get()
                .uri("/api/hello")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(GreetingResponse.class)
                .value(response -> {
                    assertThat(response.message()).isEqualTo("Hello from Open Bank");
                    assertThat(response.service()).isEqualTo("open-bank-api");
                    assertThat(response.timestamp()).isEqualTo(Instant.parse("2026-05-04T00:00:00Z"));
                });
    }

    static class FixedClockConfiguration {

        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-05-04T00:00:00Z"), ZoneOffset.UTC);
        }
    }
}
