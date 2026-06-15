package com.example.openbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {

  private final String code;
  private final String message;
  private final String details;
  private final HttpStatus httpStatus;

  public ApplicationException(
      String code, String message, String details, HttpStatus httpStatus, Throwable cause) {
    super(message, cause);
    this.code = code;
    this.message = message;
    this.details = details;
    this.httpStatus = httpStatus;
  }
}
