package com.example.openbank.greeting;

import java.time.Clock;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

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
                .map(now -> new GreetingResponse(
                        "Hello from Open Bank",
                        "open-bank-api",
                        now
                ));
    }
}
