# Open Bank

Reactive Spring Boot API project for practicing bank account service design with Java, Reactor, and PostgreSQL.

## Stack

- Java 21
- Spring Boot 3.5.x
- Spring WebFlux and Project Reactor for reactive HTTP APIs
- Spring Data R2DBC with PostgreSQL for non-blocking persistence
- Jakarta Bean Validation for request validation
- Lombok for immutable model builders and logging boilerplate
- Micrometer Tracing with OpenTelemetry baggage propagation
- Logback structured logging with trace fields in MDC
- Testcontainers with PostgreSQL for integration tests
- Gradle wrapper with Spotless formatting

## Features

- Reactive account creation endpoint:
  `POST /api/openbank/accounts/create`
- Account repository backed by PostgreSQL R2DBC.
- Client validation through the `client-id` request header.
- Account existence check by account number before insert.
- Standard response wrapper with `data`, `errors`, and `meta`.
- Application exceptions mapped from domain error definitions.

## Tracing And Logging

The service supports request correlation through these headers:

- `op-trace-id`
- `op-parent-span-id`
- `client-id`
- `op-channel`
- `op-span-id`

`TracingInterceptor` reads incoming trace headers, generates a trace id when one is missing, and stores trace fields in Reactor context and SLF4J MDC. `logback-spring.xml` includes `trace_id`, `parent_span_id`, and `span_id` in log output so related logs can be followed across reactive scheduler boundaries.

## PostgreSQL Setup

The default local database configuration is:

```text
url: r2dbc:postgresql://localhost:5432/api_test?schema=openbank
username: api_test
password: api_test
```

Start PostgreSQL with Docker:

```bash
docker run --name open-bank-postgres \
  -e POSTGRES_DB=api_test \
  -e POSTGRES_USER=api_test \
  -e POSTGRES_PASSWORD=api_test \
  -p 5432:5432 \
  -d postgres:16-alpine
```

Create the schema and account table:

```bash
docker exec -i open-bank-postgres psql -U api_test -d api_test < src/test/resources/db/account-schema.sql
```

Verify the table exists:

```bash
docker exec -it open-bank-postgres psql -U api_test -d api_test -c '\dt openbank.*'
```

To use a different database, set these environment variables before running the app:

```bash
export OPEN_BANK_R2DBC_URL='r2dbc:postgresql://localhost:5432/api_test?schema=openbank'
export OPEN_BANK_DB_USERNAME='api_test'
export OPEN_BANK_DB_PASSWORD='api_test'
```

## Run

```bash
./gradlew bootRun
```

Then call:

```bash
curl http://localhost:8080/api/hello
```

Create an account:

```bash
curl -i -X POST 'http://localhost:8080/api/openbank/accounts/create' \
  -H 'Content-Type: application/json' \
  -H 'client-id: 123456' \
  -H 'op-trace-id: local-test-trace' \
  --data '{"accountNumber":"ACC-1001","ownerName":"Robert","currency":"AUD","balance":100.00,"status":"ACTIVE"}'
```

## Test

```bash
./gradlew test
```

## IntelliJ

Open this directory from IntelliJ IDEA on Windows or through the WSL path. Import it as a Gradle project and use the included Gradle wrapper.
