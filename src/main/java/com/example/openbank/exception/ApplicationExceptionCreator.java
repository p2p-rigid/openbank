package com.example.openbank.exception;

public class ApplicationExceptionCreator {
  private final OpenbankErrorDefinition errorDefinition;
  private Throwable throwable;

  private ApplicationExceptionCreator(OpenbankErrorDefinition errorDefinition) {
    this.errorDefinition = errorDefinition;
  }

  public static ApplicationExceptionCreator of(OpenbankErrorDefinition errorDefinition) {
    return new ApplicationExceptionCreator(errorDefinition);
  }

  public ApplicationExceptionCreator withCause(Throwable throwable) {
    this.throwable = throwable;
    return this;
  }

  public ApplicationException create() {
    if (throwable != null) {
      return new ApplicationException(errorDefinition, throwable);
    }
    return new ApplicationException(errorDefinition);
  }
}
