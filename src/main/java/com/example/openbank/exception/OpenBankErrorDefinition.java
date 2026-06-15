package com.example.openbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OpenBankErrorDefinition {
  DB_ERROR("DB_ERROR", "Database error", HttpStatus.INTERNAL_SERVER_ERROR),
  CLIENT_ID_ERROR("CLIENT_ID_ERROR", "Client id is not valid", HttpStatus.BAD_REQUEST),
  ACCOUNT_EXIST_ERROR("ACCOUNT_EXIST_ERROR", "Account already exist", HttpStatus.BAD_REQUEST),
  ACCOUNT_NOT_FOUND_ERROR(
      "ACCOUNT_NOT_EXIST_ERROR", "Account does not exist", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;

  OpenBankErrorDefinition(String code, String message, HttpStatus errorStatus) {
    this.code = code;
    this.message = message;
    this.httpStatus = errorStatus;
  }
}
