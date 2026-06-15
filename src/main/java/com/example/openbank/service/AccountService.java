package com.example.openbank.service;

import com.example.openbank.model.Account;
import com.example.openbank.model.CreateAccountContext;
import com.example.openbank.model.CreateAccountResponse;
import reactor.core.publisher.Mono;

public interface AccountService {
  Mono<CreateAccountResponse> saveAccount(CreateAccountContext context);

  Mono<Boolean> isAccountExist(String accountNumber);

  Mono<Account> getAccount(String accountNumber);
}
