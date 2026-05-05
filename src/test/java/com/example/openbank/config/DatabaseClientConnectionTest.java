package com.example.openbank.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class DatabaseClientConnectionTest {

    private final DatabaseClient databaseClient;

    @Autowired
    DatabaseClientConnectionTest(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Test
    void databaseClientShouldExecuteSelectOne() {
        Mono<Integer> result = databaseClient.sql("SELECT 1")
                .map((row, metadata) -> row.get(0, Integer.class))
                .one();

        StepVerifier.create(result)
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void databaseClientShouldQueryAccountTable() {
        Mono<Long> result = databaseClient.sql("SELECT COUNT(*) FROM openbank.account")
                .map((row, metadata) -> row.get(0, Long.class))
                .one();

        StepVerifier.create(result)
                .expectNextMatches(count -> count >= 0)
                .verifyComplete();
    }
}
