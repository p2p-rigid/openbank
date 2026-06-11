package com.example.openbank.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
public class CreateAccountRequest {

  @Valid
  @NotNull(message = "Account is required")
  Account account;

  @NotBlank String clientId;
}
