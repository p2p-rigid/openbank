package com.example.openbank.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.openbank.exception.ApplicationExceptionCreator;
import com.example.openbank.exception.OpenBankErrorDefinition;
import com.example.openbank.model.Account;
import com.example.openbank.model.CreateAccountResponse;
import com.example.openbank.service.AccountService;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(AccountController.class)
class AccountControllerTest {
  private WebTestClient webTestClient;

  @MockitoBean private AccountService accountService;

  @Autowired
  AccountControllerTest(WebTestClient webTestClient) {
    this.webTestClient = webTestClient;
  }

  private static String ACCOUNT_ID = "12345678-1234-1234-1234-123456789012";
  private static ZonedDateTime CREATED_DATE = ZonedDateTime.parse("2021-01-01T00:00:00Z");
  private Account account;
  private CreateAccountResponse accountResponse;

  @BeforeEach
  void setUp() {
    this.account =
        Account.builder()
            .withAccountNumber("ACC-1001")
            .withOwnerName("Robert")
            .withCurrency("AUD")
            .build();

    this.accountResponse =
        CreateAccountResponse.builder()
            .withAccountId(ACCOUNT_ID)
            .withCreatedDate(CREATED_DATE)
            .build();
  }

  @Test
  void createAccountReturnsCreateAccountResponse() {
    when(accountService.saveAccount(any())).thenReturn(Mono.just(accountResponse));

    webTestClient
        .post()
        .uri("/api/openbank/accounts/create")
        .header("client-id", "test-request-1")
        .header("x-request-id", "test-request-1")
        .bodyValue(account)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data.accountId")
        .isEqualTo(ACCOUNT_ID)
        .jsonPath("$.data.createdDate")
        .isEqualTo("2021-01-01T00:00:00Z")
        .consumeWith(
            result -> {
              System.out.println(new String(result.getResponseBody()));
            });
  }

  @Test
  void getAccountNotFoundErrorTest() {
    when(accountService.getAccount("ACC-404"))
        .thenReturn(
            Mono.error(
                ApplicationExceptionCreator.of(OpenBankErrorDefinition.ACCOUNT_NOT_FOUND_ERROR)
                    .withDetails("Account ACC-404 does not exist")
                    .create()));
    webTestClient
        .get()
        .uri("/api/openbank/accounts/ACC-404")
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.errors[0].code")
        .isEqualTo("ACCOUNT_NOT_EXIST_ERROR")
        .jsonPath("$.errors[0].message")
        .isEqualTo("Account does not exist")
        .jsonPath("$.errors[0].details")
        .isEqualTo("Account ACC-404 does not exist");
  }
}
