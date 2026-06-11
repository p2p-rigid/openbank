package com.example.openbank.service;

import com.example.openbank.model.AccountResponse;
import com.example.openbank.model.CreateAccountContext;
import reactor.core.publisher.Mono;

public interface AccountService {
  Mono<AccountResponse> saveAccount(CreateAccountContext context);

  Mono<Boolean> isAccountExist(String accountNumber);
}
