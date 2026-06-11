package com.example.openbank.support;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class PostgresContainerSmokeTest extends AbstractPostgresContainerTest {

  @Test
  void databaseClientShouldExecuteSelectOne() {
    Mono<Integer> result =
        databaseClient.sql("SELECT 1").map((row, metadata) -> row.get(0, Integer.class)).one();
    StepVerifier.create(result).expectNext(1).verifyComplete();
  }
}
