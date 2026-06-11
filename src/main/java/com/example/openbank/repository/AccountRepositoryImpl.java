package com.example.openbank.repository;

import com.example.openbank.exception.ApplicationExceptionCreator;
import com.example.openbank.exception.OpenbankErrorDefinition;
import com.example.openbank.model.Account;
import com.example.openbank.model.CreateAccountContext;
import io.micrometer.observation.annotation.Observed;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AccountRepositoryImpl implements AccountRepository {

  private final DatabaseClient databaseClient;

  public AccountRepositoryImpl(DatabaseClient databaseClient) {
    this.databaseClient = databaseClient;
  }

  @Override
  @Observed(name = "account.save", contextualName = "saving-account-to-database")
  public Mono<CreateAccountContext> saveAccount(CreateAccountContext context) {
    final String clientId = context.getClientId();
    final Account account = context.getAccount();

    log.info("Saving account {} for client {}", account.getAccountNumber(), clientId);

    return databaseClient
        .sql(Q.INSERT_ACCOUNT_QUERY.formatted("account"))
        .bind("account_number", account.getAccountNumber())
        .bind("owner_name", account.getOwnerName())
        .bind("currency", account.getCurrency())
        .map((row, rowMetadata) -> row.get("account_id", UUID.class))
        .one()
        .switchIfEmpty(
            Mono.error(ApplicationExceptionCreator.of(OpenbankErrorDefinition.DB_ERROR).create()))
        .map(
            accountId -> {
              Account savedAccount = account.toBuilder().withAccountId(accountId).build();
              log.info("Account {} saved with id {}", savedAccount.getAccountNumber(), accountId);
              return context.toBuilder().withAccount(savedAccount).build();
            })
        .onErrorMap(
            (throwable) -> {
              log.error(
                  "Failed to save account {}: {}",
                  account.getAccountNumber(),
                  throwable.getMessage(),
                  throwable);
              return ApplicationExceptionCreator.of(OpenbankErrorDefinition.DB_ERROR).create();
            });
  }

  @Override
  public Mono<Boolean> isAccountExist(String accountNumber) {
    return databaseClient
        .sql(Q.IS_ACCOUNT_EXISTS_QUERY)
        .bind("account_number", accountNumber)
        .map((row, rowMetadata) -> row.get("exists", Boolean.class))
        .one()
        .switchIfEmpty(
            Mono.error(ApplicationExceptionCreator.of(OpenbankErrorDefinition.DB_ERROR).create()))
        .onErrorMap(
            throwable -> {
              log.error("Failed to check account existence: {}", throwable.getMessage(), throwable);
              return ApplicationExceptionCreator.of(OpenbankErrorDefinition.DB_ERROR).create();
            });
  }
}
