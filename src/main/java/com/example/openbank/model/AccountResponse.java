package com.example.openbank.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Value;

@Builder(setterPrefix = "with", toBuilder = true)
@Value
public class AccountResponse {
  String accountId;
  String accountNumber;
  BigDecimal balance;
  ZonedDateTime createdAt;
  String currency;
  String ownerName;
  AccountStatus status;

  public static AccountResponse from(Account account) {
    return AccountResponse.builder()
        .withAccountId(account.getAccountId().toString())
        .withCreatedAt(account.getCreatedAt())
        .withAccountNumber(account.getAccountNumber())
        .withCurrency(account.getCurrency())
        .withBalance(account.getBalance())
        .withOwnerName(account.getOwnerName())
        .withStatus(account.getStatus())
        .build();
  }
}
