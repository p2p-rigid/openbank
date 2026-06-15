package com.example.openbank.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import com.example.openbank.model.Account;
import com.example.openbank.model.CreateAccountContext;
import com.example.openbank.repository.AccountRepository;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

  @Mock private AccountRepository accountRepository;

  @Mock private CreateAccountValidator createAccountValidator;

  private AccountService accountService;

  @BeforeEach
  void setup() {
    accountService = new AccountServiceImpl(accountRepository, createAccountValidator);
  }

  @Test
  void shouldSaveAccount() {
    CreateAccountContext createAccountContext = createAccountContext();
    UUID accountId = UUID.randomUUID();
    CreateAccountContext savedAccountContext = savedAccountContext(accountId);

    when(createAccountValidator.validate(createAccountContext))
        .thenReturn(Mono.just(createAccountContext));
    when(accountRepository.isAccountExist(createAccountContext.getAccount().getAccountNumber()))
        .thenReturn(Mono.just(false));
    when(accountRepository.saveAccount(createAccountContext))
        .thenReturn(Mono.just(savedAccountContext));

    StepVerifier.create(accountService.saveAccount(createAccountContext))
        .assertNext(
            response -> {
              assertThat(response.getAccountId()).isEqualTo(accountId.toString());
              assertThat(response.getCreatedDate()).isNotNull();
            })
        .verifyComplete();
  }

  private static CreateAccountContext createAccountContext() {
    Account account =
        Account.builder()
            .withAccountNumber("ACC-1001")
            .withOwnerName("Robert")
            .withCurrency("AUD")
            .build();
    return CreateAccountContext.builder()
        .withAccount(account)
        .withClientId("123456")
        .withRequestHeaders(Map.of("x-request-id", "test-request-1"))
        .build();
  }

  private static CreateAccountContext savedAccountContext(UUID accountId) {
    CreateAccountContext context = createAccountContext();
    return context.toBuilder()
        .withAccount(context.getAccount().toBuilder().withAccountId(accountId).build())
        .build();
  }
}
