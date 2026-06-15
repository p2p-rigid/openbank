package com.example.openbank.model;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with", toBuilder = true)
public class CreateAccountResponse {
  String accountId;
  ZonedDateTime createdDate;
}
