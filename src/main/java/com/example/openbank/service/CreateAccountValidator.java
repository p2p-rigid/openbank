package com.example.openbank.service;

import com.example.openbank.exception.ApplicationExceptionCreator;
import com.example.openbank.exception.OpenBankErrorDefinition;
import com.example.openbank.model.CreateAccountContext;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public final class CreateAccountValidator {

  private static final List<String> validClientIds = List.of("123456", "654321");

  public Mono<CreateAccountContext> validate(CreateAccountContext createAccountContext) {
    if (!validClientIds.contains(createAccountContext.getClientId())) {
      log.error("Invalid client id {}", createAccountContext.getClientId());
      return Mono.error(
          ApplicationExceptionCreator.of(OpenBankErrorDefinition.CLIENT_ID_ERROR)
              .withDetails("Invalid client id: " + createAccountContext.getClientId())
              .create());
    }
    return Mono.just(createAccountContext);
  }
}
