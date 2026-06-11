package com.example.openbank.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
  private final OpenbankErrorDefinition errorDefinition;

  public ApplicationException(OpenbankErrorDefinition errorDefinition) {
    super(errorDefinition.getMessage());
    this.errorDefinition = errorDefinition;
  }

  public ApplicationException(OpenbankErrorDefinition errorDefinition, Throwable cause) {
    super(errorDefinition.getMessage(), cause);
    this.errorDefinition = errorDefinition;
  }
}
