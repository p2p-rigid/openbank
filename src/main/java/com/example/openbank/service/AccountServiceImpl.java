package com.example.openbank.service;

import com.example.openbank.exception.ApplicationExceptionCreator;
import com.example.openbank.exception.OpenbankErrorDefinition;
import com.example.openbank.model.AccountResponse;
import com.example.openbank.model.CreateAccountContext;
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
  public Mono<AccountResponse> saveAccount(CreateAccountContext createAccountContext) {
    final String accountNumber = createAccountContext.getAccount().getAccountNumber();

    return createAccountValidator
        .apply(createAccountContext)
        .flatMap(
            createContext ->
                accountRepository
                    .isAccountExist(accountNumber)
                    .flatMap(
                        exist -> {
                          if (exist)
                            return Mono.error(
                                ApplicationExceptionCreator.of(
                                        OpenbankErrorDefinition.ACCOUNT_EXIST_ERROR)
                                    .create());
                          return accountRepository.saveAccount(createContext);
                        })
                    .map(
                        savedContext ->
                            AccountResponse.builder()
                                .withAccountId(savedContext.getAccount().getAccountId().toString())
                                .withCreatedDate(
                                    savedContext.getAccount().getCreatedAt() != null
                                        ? savedContext.getAccount().getCreatedAt()
                                        : ZonedDateTime.now())
                                .build()));
  }

  @Override
  public Mono<Boolean> isAccountExist(String accountNumber) {
    return accountRepository.isAccountExist(accountNumber);
  }
}
