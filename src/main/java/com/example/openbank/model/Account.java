package com.example.openbank.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Value
@Builder (setterPrefix = "with", toBuilder = true)
@Jacksonized
public class Account {
    UUID accountId;

    @Valid
    @NotBlank
    String accountNumber;

    @NotBlank
    String ownerName;

    BigDecimal balance;

    @NotBlank
    String currency;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    ZonedDateTime createdAt;

    AccountStatus status;
}
