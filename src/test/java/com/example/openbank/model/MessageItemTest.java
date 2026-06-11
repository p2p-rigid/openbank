package com.example.openbank.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

class MessageItemTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testMessageItemCreation() {
    MessageItem item = MessageItem.of("Test message", "TEST_CODE", "test_field");
    assertEquals("Test message", item.getMessage());
    assertEquals("TEST_CODE", item.getCode());
    assertEquals("test_field", item.getField());
  }

  @Test
  void testMessageItemNullFields() {
    assertThrows(NullPointerException.class, () -> MessageItem.of(null, "TEST_CODE", "test_field"));
    assertThrows(
        NullPointerException.class, () -> MessageItem.of("Test message", null, "test_field"));
  }

  @Test
  void shouldSerializeToJson() throws Exception {
    String jsonMessage =
        objectMapper.writeValueAsString(MessageItem.of("Test message", "TEST_CODE", "test_field"));
    assertThat(jsonMessage).isNotNull();
    assertThat(jsonMessage).contains("Test message");
    assertThat(jsonMessage).contains("TEST_CODE");
    assertThat(jsonMessage).contains("test_field");
  }
}
