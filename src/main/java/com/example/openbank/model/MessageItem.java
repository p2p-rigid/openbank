package com.example.openbank.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageItem {
  private final String message;
  private final String code;
  private final String field;

  @JsonCreator
  private MessageItem(
      @JsonProperty("message") String message,
      @JsonProperty("code") String code,
      @JsonProperty("field") String field) {
    this.message = Objects.requireNonNull(message, "message cannot be null");
    this.code = Objects.requireNonNull(code, "code cannot be null");
    this.field = field;
  }

  public static MessageItem of(String message, String code, String field) {
    return new MessageItem(message, code, field);
  }
}
