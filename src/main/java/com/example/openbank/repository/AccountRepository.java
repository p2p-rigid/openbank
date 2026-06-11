package com.example.openbank.repository;

import com.example.openbank.model.CreateAccountContext;
import reactor.core.publisher.Mono;

public interface AccountRepository {
  Mono<CreateAccountContext> saveAccount(CreateAccountContext context);

  Mono<Boolean> isAccountExist(String accountNumber);
}
