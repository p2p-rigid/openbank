package com.example.openbank.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ResponseContainerTest {

  @Test
  void shouldBuild() {
    MessageItem messageItem = MessageItem.of("Test message", "TEST_CODE", "test_field");
    List<MessageItem> errors = List.of(messageItem);
    ResponseContainer<String> responseContainer =
        ResponseContainer.<String>newBuild()
            .withData("test data")
            .withErrors(errors)
            .withMeta(Map.of("key", "value"))
            .build();
    assertThat(responseContainer).isNotNull();
    assertThat(responseContainer.getData()).isEqualTo("test data");
    assertThat(responseContainer.getErrors()).isEqualTo(errors);
    assertThat(responseContainer.getMeta()).isEqualTo(Map.of("key", "value"));
  }

  @Test
  void shouldDeserialize() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    String json =
        """
            {
              "data": "test data",
              "errors": [
                {
                  "message": "Test message",
                  "code": "TEST_CODE",
                  "field": "test_field"
                }
              ],
              "meta": {
                "key": "value"
              }
            }
            """;

    ResponseContainer<String> responseContainer =
        objectMapper.readValue(json, new TypeReference<>() {});
    assertThat(responseContainer).isNotNull();
    assertThat(responseContainer.getData()).isEqualTo("test data");
    assertThat(responseContainer.getErrors()).hasSize(1);
    assertThat(responseContainer.getMeta()).isEqualTo(Map.of("key", "value"));
  }
}
