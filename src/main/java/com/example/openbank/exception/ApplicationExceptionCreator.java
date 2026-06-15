package com.example.openbank.exception;

public class ApplicationExceptionCreator {

  private final OpenBankErrorDefinition errorDefinition;
  private Throwable throwable;
  private String details;

  private ApplicationExceptionCreator(OpenBankErrorDefinition errorDefinition) {
    this.errorDefinition = errorDefinition;
  }

  public static ApplicationExceptionCreator of(OpenBankErrorDefinition errorDefinition) {
    return new ApplicationExceptionCreator(errorDefinition);
  }

  public ApplicationExceptionCreator withCause(Throwable throwable) {
    this.throwable = throwable;
    return this;
  }

  public ApplicationExceptionCreator withDetails(String details) {
    this.details = details;
    return this;
  }

  public ApplicationException create() {
    return new ApplicationException(
        errorDefinition.getCode(),
        errorDefinition.getMessage(),
        details,
        errorDefinition.getHttpStatus(),
        throwable);
  }
}
