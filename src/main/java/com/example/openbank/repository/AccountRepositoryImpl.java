package com.example.openbank.repository;

import com.example.openbank.exception.ApplicationExceptionCreator;
import com.example.openbank.exception.OpenBankErrorDefinition;
import com.example.openbank.model.Account;
import com.example.openbank.model.AccountStatus;
import com.example.openbank.model.CreateAccountContext;
import io.micrometer.observation.annotation.Observed;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
        .map(this::mappAccountId)
        .one()
        .switchIfEmpty(
            Mono.error(ApplicationExceptionCreator.of(OpenBankErrorDefinition.DB_ERROR).create()))
        .map(accountId -> getCreateAccountContext(context, accountId, account))
        .onErrorMap(this::mapError);
  }

  @Override
  public Mono<Account> getAccount(String accountNumber) {
    return databaseClient
        .sql(Q.SELECT_ACCOUNT)
        .bind("account_number", accountNumber)
        .map(
            (row, rowMetadata) -> {
              return Account.builder()
                  .withAccountNumber(row.get("account_number", String.class))
                  .withOwnerName(row.get("owner_name", String.class))
                  .withCurrency(row.get("currency", String.class))
                  .withAccountId(row.get("account_id", UUID.class))
                  .withBalance(row.get("balance", BigDecimal.class))
                  .withCreatedAt(row.get("created_at", ZonedDateTime.class))
                  .withStatus(AccountStatus.valueOf(row.get("status", String.class)))
                  .build();
            })
        .one()
        .onErrorMap(this::mapError);
  }

  private static CreateAccountContext getCreateAccountContext(
      CreateAccountContext context, UUID accountId, Account account) {
    Account savedAccount = account.toBuilder().withAccountId(accountId).build();
    log.info("Account {} saved with id {}", savedAccount.getAccountNumber(), accountId);
    return context.toBuilder().withAccount(savedAccount).build();
  }

  private Throwable mapError(Throwable throwable) {
    return ApplicationExceptionCreator.of(OpenBankErrorDefinition.DB_ERROR)
        .withCause(throwable)
        .create();
  }

  private UUID mappAccountId(Row row, RowMetadata rowMetadata) {
    return row.get("account_id", UUID.class);
  }

  @Override
  public Mono<Boolean> isAccountExist(String accountNumber) {
    return databaseClient
        .sql(Q.IS_ACCOUNT_EXISTS_QUERY)
        .bind("account_number", accountNumber)
        .map((row, rowMetadata) -> row.get("exists", Boolean.class))
        .one()
        .switchIfEmpty(
            Mono.error(ApplicationExceptionCreator.of(OpenBankErrorDefinition.DB_ERROR).create()))
        .onErrorMap(
            throwable -> {
              log.error("Failed to check account existence: {}", throwable.getMessage(), throwable);
              return ApplicationExceptionCreator.of(OpenBankErrorDefinition.DB_ERROR)
                  .withCause(throwable)
                  .withDetails("Failed to check account existence for account: " + accountNumber)
                  .create();
            });
  }
}
