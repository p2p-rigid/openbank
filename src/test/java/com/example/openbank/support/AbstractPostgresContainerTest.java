package com.example.openbank.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractPostgresContainerTest {

  @Autowired protected DatabaseClient databaseClient;

  @SuppressWarnings("resource")
  protected static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("api_test")
          .withUsername("api_test")
          .withPassword("api_test")
          .withInitScript("db/account-schema.sql");

  static {
    POSTGRES.start();
  }

  @DynamicPropertySource
  static void registerPostgresProperties(DynamicPropertyRegistry registry) {
    registry.add("app.r2dbc.url", AbstractPostgresContainerTest::r2dbcUrl);
    registry.add("app.r2dbc.username", POSTGRES::getUsername);
    registry.add("app.r2dbc.password", POSTGRES::getPassword);
  }

  private static String r2dbcUrl() {
    return "r2dbc:postgresql://%s:%d/%s?schema=openbank"
        .formatted(
            POSTGRES.getHost(),
            POSTGRES.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
            POSTGRES.getDatabaseName());
  }
}
