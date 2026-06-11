package com.example.openbank.greeting;

import java.time.Clock;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api")
public class GreetingController {

  private final Clock clock;

  public GreetingController(Clock clock) {
    this.clock = clock;
  }

  @GetMapping("/hello")
  public Mono<GreetingResponse> hello() {
    return Mono.fromSupplier(() -> Instant.now(clock))
        .doOnSubscribe(subscription -> log.info("Handling greeting request"))
        .map(now -> new GreetingResponse("Hello from Open Bank", "open-bank-api", now))
        .doOnSuccess(response -> log.info("Greeting response ready: {}", response.message()));
  }
}
