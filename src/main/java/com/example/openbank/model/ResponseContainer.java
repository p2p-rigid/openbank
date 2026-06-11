package com.example.openbank.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseContainer<T> {
  private final T data;
  private final List<MessageItem> errors;
  private final Map<String, Object> meta;

  @JsonCreator
  private ResponseContainer(
      @JsonProperty("data") T data,
      @JsonProperty("errors") List<MessageItem> errors,
      @JsonProperty("meta") Map<String, Object> meta) {
    this.data = data;
    this.errors = errors;
    this.meta = meta;
  }

  public static <T> Builder<T> newBuild() {
    return new Builder<>();
  }

  public static class Builder<T> {
    private T data;
    private List<MessageItem> errors;
    private Map<String, Object> meta;

    public Builder<T> withData(T data) {
      this.data = data;
      return this;
    }

    public Builder<T> withErrors(List<MessageItem> errors) {
      if (errors != null && errors.isEmpty()) {
        this.errors = List.of();
      } else {
        this.errors = errors;
      }
      return this;
    }

    public Builder<T> withMeta(Map<String, Object> meta) {
      if (meta != null && meta.isEmpty()) {
        this.meta = Map.of();
      } else {
        this.meta = meta;
      }
      return this;
    }

    public ResponseContainer<T> build() {
      return new ResponseContainer<>(data, errors, meta);
    }
  }
}
