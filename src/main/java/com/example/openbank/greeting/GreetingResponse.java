package com.example.openbank.greeting;

import java.time.Instant;

public record GreetingResponse(
        String message,
        String service,
        Instant timestamp
) {
}
