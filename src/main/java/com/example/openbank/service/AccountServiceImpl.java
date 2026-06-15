package com.example.openbank.service;

import com.example.openbank.exception.ApplicationExceptionCreator;
import com.example.openbank.exception.OpenBankErrorDefinition;
import com.example.openbank.model.Account;
import com.example.openbank.model.CreateAccountContext;
import com.example.openbank.model.CreateAccountResponse;
import com.example.openbank.repository.AccountRepository;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service("accountService")
public class AccountServiceImpl implements AccountService {
  private final AccountRepository accountRepository;
  private final CreateAccountValidator createAccountValidator;

  public AccountServiceImpl(
      AccountRepository accountRepository, CreateAccountValidator createAccountValidator) {
    this.accountRepository = accountRepository;
    this.createAccountValidator = createAccountValidator;
  }

  @Override
  @Transactional
  public Mono<CreateAccountResponse> saveAccount(CreateAccountContext createAccountContext) {
    return Mono.just(createAccountContext)
        .flatMap(createAccountValidator::validate)
        .flatMap(this::checkIfAccountExist)
        .flatMap(accountRepository::saveAccount)
        .map(AccountServiceImpl::createAccountResponse);
  }

  @Override
  public Mono<Account> getAccount(String accountNumber) {
    return accountRepository
        .getAccount(accountNumber)
        .switchIfEmpty(
            Mono.error(
                ApplicationExceptionCreator.of(OpenBankErrorDefinition.ACCOUNT_NOT_FOUND_ERROR)
                    .withDetails("Account with account number " + accountNumber + " not found")
                    .create()));
  }

  @Override
  public Mono<Boolean> isAccountExist(String accountNumber) {
    return accountRepository.isAccountExist(accountNumber);
  }

  private Mono<CreateAccountContext> checkIfAccountExist(CreateAccountContext context) {
    final String accountNumber = context.getAccount().getAccountNumber();
    return accountRepository
        .isAccountExist(accountNumber)
        .flatMap(
            exist -> {
              if (exist) {
                return Mono.error(
                    ApplicationExceptionCreator.of(OpenBankErrorDefinition.ACCOUNT_EXIST_ERROR)
                        .withDetails(
                            "Account with account number " + accountNumber + " already exist")
                        .create());
              }
              return Mono.just(context);
            });
  }

  private static CreateAccountResponse createAccountResponse(CreateAccountContext savedContext) {
    return CreateAccountResponse.builder()
        .withAccountId(savedContext.getAccount().getAccountId().toString())
        .withCreatedDate(
            savedContext.getAccount().getCreatedAt() != null
                ? savedContext.getAccount().getCreatedAt()
                : ZonedDateTime.now())
        .build();
  }
}
