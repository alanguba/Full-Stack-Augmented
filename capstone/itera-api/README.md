# Itera Backend

Itera Backend is the Spring Boot REST service for the Itera travel itinerary application. It provides itinerary generation, persistence, user management, image lookup and integrations used by the Angular frontend.

---

## Overview
- This repository contains the Spring Boot backend for Itera. It supports itinerary generation (OpenAI), travel plan persistence, user/role management, destination image lookups (Pexels), and email workflows (OTP, welcome, reset).
- The backend exposes REST endpoints consumed by a frontend application and integrates with third-party APIs where configured.

---

## Key Features
- REST API endpoints for plans, itineraries, users, roles, destination images and auth.
- Itinerary generation using OpenAI Responses API (configured via `OpenAiRestClientConfig`).
- Destination image retrieval via Pexels (`PexelsRestClientConfig`).
- Persistence with Spring Data JPA / Hibernate and schema/data SQL initialization.
- Request validation using Jakarta Validation annotations on DTOs.
- OTP-based login flow and JWT issuance/validation for authentication.
- Role-based authorization (example: administrative user management endpoints).
- Email sending via Spring `JavaMailSender` for OTP and notifications.
- H2 in-memory DB support and H2 Console for local development.

---

## Tech Stack
- Java 17
- Spring Boot 4.0.5
- Spring Web (REST)
- Spring Data JPA (Hibernate)
- Spring Security
- Jakarta Validation
- H2 database and H2 Console
- Maven build
- Lombok
- jjwt (io.jsonwebtoken) for JWT handling
- Spring JavaMail
- Spring `RestClient` for external API calls
- JUnit 5 for tests

---

## Prerequisites
- Java 17 JDK
- Maven 3.6+ (or recent)
- SMTP account for email delivery
- OpenAI and Pexels API keys for integrations

---

## Installation
1. Clone the repository:
```bash
git clone <repository-url>
cd itera-api
```
2. Configure environment variables or update `src/main/resources/application.properties` (see Configuration).
3. Build the project:
```bash
mvn clean package
```

---

## Running Locally
- Run with Maven:
```bash
mvn spring-boot:run
```
- Or run packaged artifact:
```bash
java -jar target/itera-0.0.1-SNAPSHOT.jar
```
- Default port: `8080` (Spring Boot default).
- H2 Console: `/h2-console` (enabled in development configuration).

---

## Configuration
- Primary configuration file: [src/main/resources/application.properties](src/main/resources/application.properties)
- Important environment properties (placeholders in `application.properties`):
  - `OPEN_AI_TOKEN` → `openai.api.api-key`
  - `PEXELS_TOKEN` → `pexels.api-key`
  - `SIGNING_KEY` → `jwt.signing.key`
  - `DB_USERNAME`, `DB_PASSWORD` → datasource credentials (H2 used by default)
  - `EMAIL_SENDER`, `PASSWORD_SENDER` → SMTP account

---

## Available Endpoints (summary)
- `POST /login` (filter-driven): provide `username`/`password` headers to receive OTP; provide `code` to exchange OTP for JWT in `Authorization` header.
- `POST /api/auth/reset-password` — reset password / OTP workflow.
- `POST /api/itinerario/generate` — generate itinerary from `TripPlanRequest` (calls OpenAI).
- `POST /api/plan/save` — persist a complete itinerary/plan (`ItinerarySaveRequest`).
- `GET /api/plan/get?planId={id}` — fetch detailed plan.
- `GET /api/plan?userId={id}` — list plans for a user.
- `GET /api/destination-images?place={place}` — returns image metadata from Pexels.
- `GET /api/rol`, `POST /api/rol`, `PATCH /api/rol` — role management.
- `GET /api/usuario/getAll`, `POST /api/usuario/add`, `PUT /api/usuario/update`, `PATCH /api/usuario/status` — user management (admin-only where configured).

Notes: controllers enable CORS for `http://localhost:4200` for frontend development.

---

## Request / Response Model
- API models are defined as Java records under `com.api.agb.itera.dto`.
- Validation via annotations (`@NotBlank`, `@Min`, `@Size`, etc.) ensures request correctness; invalid requests return standard Spring validation errors.
- `ItineraryResponse` describes the expected JSON output for generated itineraries (destination, days, pace, itinerary/day plan/stops).
- Errors: services may throw runtime exceptions for not-found or business rules; JWT errors return `401` with textual messages in filters.

---

## Project Structure
- `controller` — HTTP endpoints and request orchestration
- `service` — business logic, persistence orchestration and external API integrations
- `repository` — Spring Data JPA repositories
- `model` — JPA entities (Plan, Itinerario, Lugar, Actividad, Usuario, Rol)
- `dto` — API request/response models and validation
- `config` — RestClient beans, security and CORS configuration
- `filter` — authentication filters (login and JWT validation)
- `auth` / `auth.providers` — custom authentication tokens and providers
- `resources` — `application.properties`, `schema.sql`, `data.sql`, static assets

---

## Architecture Overview
- Layered controller → service → repository architecture.
- DTOs separate API contract from persistence entities.
- Transactions: `@Transactional` used for write operations (e.g., user and plan creation).
- Validation leverages Jakarta Validation and is applied at controller boundaries.
- Security: two-stage login (credentials → OTP → JWT) implemented with custom filters/providers and `jjwt` for HMAC-signed tokens.
- External integrations implemented via `RestClient` beans (OpenAI and Pexels) to keep code testable and configurable.

---

## Database
- Default development DB: H2 in-memory (configured in `application.properties`).
- Schema and seed data executed from:
  - [src/main/resources/schema.sql](src/main/resources/schema.sql)
  - [src/main/resources/data.sql](src/main/resources/data.sql)
- ORM: Spring Data JPA / Hibernate.
- No Flyway / Liquibase migrations were found; add migrations for production environments.

---

## Security
- Authentication flow:
  1. Send `username` and `password` in request headers to `/login` — if valid, backend generates and emails an OTP.
  2. Call `/login` again with `username` and `code` (OTP header) — backend returns a JWT in the `Authorization` response header.
- `JwtAuthenticationFilter` validates JWTs on incoming requests and sets `SecurityContext`.
- Role-based access configured in `SecurityConfig` (e.g., `/api/usuario/**` requires role `ADMINISTRADOR`).
- JWT signing key and expiration are configured via `application.properties` (`jwt.signing.key`, `jwt.expiration.ms`).

---

## External Integrations
- OpenAI (Responses API): used for itinerary generation (`OpenAiRestClientConfig`).
- Pexels: used for destination images (`PexelsRestClientConfig`).
- SMTP (JavaMailSender): used to send OTP and welcome/reset emails.

---

## Error Handling
- Validation errors are surfaced by Spring MVC when DTO constraints fail.
- Custom exceptions exist (e.g., `CorreoDuplicadoException`) and will propagate with default Spring error mapping unless a global handler is added.
- JWT errors are handled in `JwtAuthenticationFilter` and result in `401` responses with short textual messages.

---

## Build
- Package with Maven:
```bash
mvn clean package
```
- Artifact: `target/itera-0.0.1-SNAPSHOT.jar` (as defined in `pom.xml`).

---

## Testing
- Run tests:
```bash
mvn test
```
- Tests use JUnit 5 and SpringBootTest for the application context.

---

## API Documentation
- No Swagger/OpenAPI configuration detected in the repository.
- Placeholder: [Add OpenAPI/Swagger configuration and documentation URL]

---

## Troubleshooting
- Confirm Java 17 is installed.
- Ensure environment variables defined in `application.properties` are set (OpenAI, Pexels, JWT signing key, SMTP credentials).
- If H2 console is inaccessible, verify `spring.h2.console.enabled` and `spring.h2.console.path` in `application.properties`.
- JWT problems: verify `jwt.signing.key` length and validity.
- Email delivery issues: verify SMTP credentials and provider settings.

---

## Contributing
- Fork the repository, create a feature branch, add tests and open a pull request with a clear description.
- Update `schema.sql` and `data.sql` only for local dev seed changes; use migration tooling for production schema changes.

---

## License

This project is licensed under the MIT License © 2026 Alan Gutierrez.

You are free to use, modify, and distribute this software with proper attribution.  
See the [LICENSE](./LICENSE) file for more details.

