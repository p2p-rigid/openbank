package com.example.openbank.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class AccountResponseTest {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Test
  void shouldBuild() {
    ZonedDateTime now = ZonedDateTime.now();
    CreateAccountResponse response =
        CreateAccountResponse.builder().withAccountId("acc-123").withCreatedDate(now).build();

    assertThat(response.getAccountId()).isEqualTo("acc-123");
    assertThat(response.getCreatedDate()).isEqualTo(now);
  }

  @Test
  void shouldBeEqual() {
    ZonedDateTime now = ZonedDateTime.now();
    CreateAccountResponse a =
        CreateAccountResponse.builder().withAccountId("acc-123").withCreatedDate(now).build();
    CreateAccountResponse b =
        CreateAccountResponse.builder().withAccountId("acc-123").withCreatedDate(now).build();

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  void shouldNotBeEqual() {
    ZonedDateTime now = ZonedDateTime.now();
    CreateAccountResponse a =
        CreateAccountResponse.builder().withAccountId("acc-123").withCreatedDate(now).build();
    CreateAccountResponse b =
        CreateAccountResponse.builder().withAccountId("acc-456").withCreatedDate(now).build();

    assertThat(a).isNotEqualTo(b);
  }

  @Test
  void shouldSerializeToJson() throws Exception {
    ZonedDateTime now = ZonedDateTime.now();
    CreateAccountResponse response =
        CreateAccountResponse.builder().withAccountId("acc-123").withCreatedDate(now).build();

    String json = objectMapper.writeValueAsString(response);

    assertThat(json).isNotNull();
    assertThat(json).contains("acc-123");
  }

  @Test
  void shouldSupportToBuilder() {
    ZonedDateTime now = ZonedDateTime.now();
    CreateAccountResponse original =
        CreateAccountResponse.builder().withAccountId("acc-123").withCreatedDate(now).build();

    CreateAccountResponse modified = original.toBuilder().withAccountId("acc-999").build();

    assertThat(modified.getAccountId()).isEqualTo("acc-999");
    assertThat(modified.getCreatedDate()).isEqualTo(now);
  }
}
