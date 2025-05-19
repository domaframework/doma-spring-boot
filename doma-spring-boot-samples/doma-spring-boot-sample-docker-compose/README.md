# Doma Spring Boot Sample with Docker Compose

This sample demonstrates how to use Doma with Spring Boot's Docker Compose support.

## Requirements

- Docker
- Docker Compose

## How it works

This sample uses Spring Boot's built-in Docker Compose support, which automatically:

1. Detects the `compose.yaml` file in the project root
2. Starts the PostgreSQL container defined in the compose file
3. Configures the application to connect to the database

The `spring-boot-docker-compose` dependency enables this functionality, allowing the application to seamlessly integrate with Docker Compose services.

## Running the sample

From the sample directory, run:

```bash
./mvnw spring-boot:run
```

Spring Boot will automatically:
- Start the PostgreSQL container defined in compose.yaml
- Configure the application to connect to the database
- Run the application

## Using the application

Once the application is running, you can access it at http://localhost:8080

### API Endpoints

- `GET /` - List all messages
- `GET /?text=hello` - Add a new message with the text "hello"

## Configuration

The PostgreSQL configuration is defined in the `compose.yaml` file:

```yaml
services:
  postgres:
    image: 'postgres:latest'
    environment:
      - 'POSTGRES_DB=${POSTGRES_DB}'
      - 'POSTGRES_PASSWORD=${POSTGRES_PASSWORD}'
      - 'POSTGRES_USER=${POSTGRES_USER}'
    ports:
      - '5432'
```

Before running the application, you should set the following environment variables:
- `POSTGRES_DB`: The name of the PostgreSQL database (e.g., "domadb")
- `POSTGRES_USER`: The PostgreSQL username (e.g., "doma")
- `POSTGRES_PASSWORD`: The PostgreSQL password

Spring Boot automatically configures the application to connect to this database.

## Notes

- No manual configuration of database connection properties is needed
- The schema.sql file is automatically executed when the application starts
- Spring Boot manages the lifecycle of the Docker containers
