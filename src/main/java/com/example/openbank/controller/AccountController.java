package com.example.openbank.controller;

import com.example.openbank.R;
import com.example.openbank.model.Account;
import com.example.openbank.model.AccountResponse;
import com.example.openbank.model.CreateAccountContext;
import com.example.openbank.model.ResponseContainer;
import com.example.openbank.service.AccountService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping("/api/openbank/accounts")
public class AccountController {

  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PostMapping("/create")
  public Mono<ResponseEntity<ResponseContainer<AccountResponse>>> createAccount(
      @RequestBody @Valid Account account,
      @RequestHeader(name = R.Headers.CLIENT_ID, required = true) String clientId,
      @RequestHeader Map<String, String> requestHeaders) {
    CreateAccountContext createAccountContext =
        CreateAccountContext.builder()
            .withAccount(account)
            .withClientId(clientId)
            .withRequestHeaders(requestHeaders)
            .build();
    return accountService
        .saveAccount(createAccountContext)
        .map(
            accountResponse ->
                ResponseContainer.<AccountResponse>newBuild().withData(accountResponse).build())
        .map(ResponseEntity::ok);
  }
}
