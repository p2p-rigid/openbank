package com.example.openbank.exception;

import com.example.openbank.model.MessageItem;
import com.example.openbank.model.ResponseContainer;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ResponseContainer<Void>> handleException(
      ApplicationException applicationException) {
    MessageItem errors =
        MessageItem.of(
            applicationException.getMessage(),
            applicationException.getCode(),
            null,
            applicationException.getDetails());

    ResponseContainer<Void> body =
        ResponseContainer.<Void>newBuild().withErrors(List.of(errors)).build();
    return ResponseEntity.status(applicationException.getHttpStatus()).body(body);
  }
}
