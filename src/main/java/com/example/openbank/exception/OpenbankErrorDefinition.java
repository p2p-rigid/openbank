package com.example.openbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OpenbankErrorDefinition {
  DB_ERROR("DB_ERROR", "Database error", HttpStatus.INTERNAL_SERVER_ERROR),
  CLIENT_ID_ERROR("CLIENT_ID_ERROR", "Client id is not valid", HttpStatus.BAD_REQUEST),
  ACCOUNT_EXIST_ERROR("ACCOUNT_EXIST_ERROR", "Account already exist", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus errorStatus;

  OpenbankErrorDefinition(String code, String message, HttpStatus errorStatus) {
    this.code = code;
    this.message = message;
    this.errorStatus = errorStatus;
  }
}
