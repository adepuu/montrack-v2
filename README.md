## Project Overview 

MonTrack is a money tracking application that helps users manage their personal finances. Users can track their income, expenses, create budgets, and analyze their spending habits. The application provides features like user authentication, wallet management, and transaction tracking.

## Clean Architecture

This project implements Clean Architecture as proposed by Robert C. Martin (Uncle Bob). Clean Architecture emphasizes separation of concerns and dependency rules that enable the creation of systems that are:

- **Independent of frameworks**: The system doesn't depend on the existence of specific libraries or frameworks.
- **Testable**: Business rules can be tested without UI, database, web server, or any external element.
- **Independent of UI**: The UI can change without changing the rest of the system.
- **Independent of database**: Business rules aren't bound to a specific database implementation.
- **Independent of any external agency**: Business rules don't know anything about the outside world.

### Architectural Layers

The project is structured into the following layers (from innermost to outermost):

1. **Domain Layer**: Contains enterprise-wide business rules and entities. It is the core of the application and has no dependencies on other layers.
   - Entities, Value Objects, Domain Services, Repository Interfaces

2. **Application Layer**: Contains application-specific business rules. It orchestrates the flow of data to and from entities.
   - Use Cases, Application Services, DTOs

3. **Infrastructure Layer**: Contains implementations for external resources such as databases, web services, or file systems.
   - Repository Implementations, External Service Adapters, ORM Entities

4. **Presentation Layer**: Contains UI components and exposes the system to the outside world.
   - Controllers, View Models, API Endpoints

## Project Structure

```
src/main/java/com/adepuu/montrack_v2/
├── auth/                    # Authentication module
│   ├── application/         # Application services and use cases
│   ├── domain/              # Domain entities and business rules
│   ├── infrastructure/      # Repository implementations
│   └── presentation/        # REST controllers and DTOs
├── common/                  # Shared components across modules
├── wallet/                  # Wallet management module
└── Application.java         # Main application entry point
```

### Key Modules

- **Auth Module**: Handles user authentication, registration, and profile management.
- **Wallet Module**: Manages user wallets, transactions, and financial records.
- **Common Module**: Contains shared utilities, base classes, and cross-cutting concerns.

## Clean Architecture Flow

1. **Request Flow**:
   - External request → Controller → Use Case → Entity → Repository
   
2. **Response Flow**:
   - Repository → Entity → Use Case → Controller → External response

## Dependency Rule

The fundamental rule of Clean Architecture is that dependencies can only point inward. This means:

- Outer layers can depend on inner layers
- Inner layers cannot depend on outer layers
- Inner layers don't know that outer layers exist

In our implementation:
- Domain doesn't know about Application, Infrastructure, or Presentation
- Application knows about Domain but not Infrastructure or Presentation
- Infrastructure and Presentation know about Application and Domain

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven
- PostgreSQL 17 (or your configured database)

### Setup

1. Clone the repository:
   ```
   git clone https://github.com/adepuu/montrack-v2.git
   ```

2. Configure your database in `application.properties` or using environment variables.

3. Build the project:
   ```
   ./mvnw clean install
   ```

4. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

## API Documentation

API documentation is available at `/swagger-ui.html` when the application is running.

## Technologies Used

- **Spring Boot**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access
- **PostgreSQL**: Database (configurable)
- **Maven**: Build tool
- **Docker**: Containerization (optional)

## Educational Notes for Students

### Clean Architecture Benefits

1. **Maintainability**: Isolating changes to specific layers makes the system easier to maintain.
2. **Testability**: Business logic can be tested independently of external dependencies.
3. **Flexibility**: External components can be swapped with minimal impact on the system.
4. **Separation of Concerns**: Each layer has a specific responsibility, making the code more organized.

### Implementation Considerations

While this project demonstrates clean architecture principles, there are practical considerations:

1. **Pragmatic Approach**: Sometimes strict adherence to architecture principles may be relaxed for practical reasons.
2. **Boilerplate Code**: Clean architecture may introduce additional abstractions and mappings.
3. **Learning Curve**: Understanding the flow of data through multiple layers requires practice.

### What to Observe

1. Notice how domain entities are pure business objects without framework dependencies.
2. Observe how the application layer orchestrates operations without knowing implementation details.
3. See how the infrastructure layer provides concrete implementations of the interfaces defined in the domain layer.
4. Understand how the presentation layer transforms domain/application data into formats suitable for the UI/API.

*Note: This project is designed for educational purposes to demonstrate clean architecture principles. While it strives to follow best practices, certain simplifications or adaptations may be present for clarity.*
