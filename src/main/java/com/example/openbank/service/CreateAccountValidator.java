package com.example.openbank.service;

import com.example.openbank.exception.ApplicationExceptionCreator;
import com.example.openbank.exception.OpenbankErrorDefinition;
import com.example.openbank.model.CreateAccountContext;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CreateAccountValidator
    implements Function<CreateAccountContext, Mono<CreateAccountContext>> {

  private static final List<String> validClientIds = List.of("123456", "654321");

  @Override
  public Mono<CreateAccountContext> apply(CreateAccountContext createAccountContext) {
    if (!validClientIds.contains(createAccountContext.getClientId())) {
      log.error("Invalid client id {}", createAccountContext.getClientId());
      return Mono.error(
          ApplicationExceptionCreator.of(OpenbankErrorDefinition.CLIENT_ID_ERROR).create());
    }
    return Mono.just(createAccountContext);
  }
}
