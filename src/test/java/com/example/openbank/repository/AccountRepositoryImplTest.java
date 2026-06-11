package com.example.openbank.repository;

import com.example.openbank.model.Account;
import com.example.openbank.model.CreateAccountContext;
import com.example.openbank.support.AbstractPostgresContainerTest;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class AccountRepositoryImplTest extends AbstractPostgresContainerTest {

  private final AccountRepository accountRepository;

  @Autowired
  public AccountRepositoryImplTest(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  private static CreateAccountContext createAccountContext() {
    Account account =
        Account.builder()
            .withAccountNumber("ACC-1001")
            .withOwnerName("Robert")
            .withCurrency("AUD")
            .build();

    return CreateAccountContext.builder()
        .withClientId("client-1")
        .withRequestHeaders(Map.of("x-request-id", "test-request-1"))
        .withAccount(account)
        .build();
  }

  @Test
  void shouldSaveAccount() {
    CreateAccountContext context = createAccountContext();
    Mono<SavedAccount> result =
        accountRepository
            .saveAccount(context)
            .then(
                databaseClient
                    .sql(
                        """
                                        SELECT account_number, owner_name, currency
                                        FROM account WHERE account_number = :accountNumber
                                        """)
                    .bind("accountNumber", context.getAccount().getAccountNumber())
                    .map(
                        (row, metadata) ->
                            new SavedAccount(
                                row.get("account_number", String.class),
                                row.get("owner_name", String.class),
                                row.get("currency", String.class)))
                    .one());

    StepVerifier.create(result)
        .expectNext(new SavedAccount("ACC-1001", "Robert", "AUD"))
        .verifyComplete();
  }

  private record SavedAccount(String accountNumber, String ownerName, String currency) {}

  @AfterEach
  void tearDown() {
    databaseClient.sql("DELETE FROM account").fetch().rowsUpdated().block();
  }
}
