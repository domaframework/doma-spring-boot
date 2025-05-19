# Doma Spring Boot Sample with TestContainers

This sample demonstrates how to use [TestContainers](https://www.testcontainers.org/) with Doma and Spring Boot.

## Running the Sample

To run this sample, you need:
- Java 17 or later
- Maven
- Docker (for running TestContainers)

### Build and Run Tests

```bash
./mvnw clean test
```

## Features Demonstrated

- Using TestContainers to start a PostgreSQL database for tests
- Integration with Spring Boot's test support for TestContainers
- Running Doma queries against a real PostgreSQL database in tests
- Configuration for both development (H2) and test (PostgreSQL) environments
