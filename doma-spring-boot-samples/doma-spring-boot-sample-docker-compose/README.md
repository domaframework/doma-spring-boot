# Doma Spring Boot Sample with Docker Compose

This sample demonstrates how to use Doma with Spring Boot using Docker Compose.

## Requirements

- Docker
- Docker Compose

## Running the sample

From the sample directory, run:

```bash
docker compose up
```

This will:
1. Start a PostgreSQL database container
2. Build the Spring Boot application
3. Start the application container linked to the database

## Using the application

Once both containers are running, you can access the application at http://localhost:8080

### API Endpoints

- `GET /` - List all messages
- `GET /?text=hello` - Add a new message with the text "hello"

## Running the application locally

If you want to run the application locally while using the Docker PostgreSQL database:

1. Start only the database container:
   ```bash
   docker compose up postgres
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Notes

- The PostgreSQL data is persisted in a Docker volume
- The application connects to PostgreSQL using environment variables with default values
- You can customize the database configuration by setting the following environment variables:
  - `POSTGRES_DB`: Database name (default: domadb)
  - `POSTGRES_USER`: Database username (default: doma)
  - `POSTGRES_PASSWORD`: Database password (default: changeme)
- The schema.sql file is automatically executed when the application starts

## Security Note

For production use, always set secure passwords through environment variables rather than using the defaults.
