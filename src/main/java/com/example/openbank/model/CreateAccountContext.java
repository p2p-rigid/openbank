package com.example.openbank.model;

import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder(setterPrefix = "with", toBuilder = true)
public class CreateAccountContext {
  String clientId;
  Map<String, String> requestHeaders;
  Account account;
}
