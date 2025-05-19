# Doma Spring Boot Sample with TestContainers

This sample demonstrates how to use [TestContainers](https://www.testcontainers.org/) with Doma and Spring Boot.

## Running the Sample

To run this sample, you need:
- Java 17 or later
- Maven
- Docker (for running TestContainers)

### Build and Run Tests

This project uses the Maven Wrapper, so you don't need to install Maven separately.

```bash
./mvnw clean test
```

### Run with TestContainers

```bash
./mvnw spring-boot:test-run
```

This will start the application with a PostgreSQL container.

## Features Demonstrated

- Using TestContainers to start a PostgreSQL database for tests
- Spring Boot's native TestContainers integration with `@ServiceConnection`
- Using TestContainers at development time with `SpringApplication.from()`
- Running Doma queries against a real PostgreSQL database in tests
- Configuration for PostgreSQL database in both development and test environments
