# GitHub Copilot Instructions for Foods Application

## Project Context
This is a Spring Boot application for food management operations built with Java 21 and Spring Boot 4.0.1.

## Common Rules
- Do not add any comments that are not necessary for understanding the code
- Do not add any emoji, icon to the generated code
- Follow best practices for Spring Boot development
- Ensure code is clean, maintainable, and well-documented
- Write unit and integration tests for all new features
- Use dependency injection wherever possible
- Handle exceptions gracefully and provide meaningful error messages

## Code Style Guidelines
- Use Java 21 features when appropriate
- Follow Spring Boot best practices
- Use meaningful variable and method names
- Add proper JavaDoc comments for public methods
- Follow standard Java naming conventions (camelCase for methods/variables, PascalCase for classes)

## Architecture Guidelines
- Follow MVC pattern with Controllers, Services, and Repositories
- Use Spring annotations (@RestController, @Service, @Repository)
- Implement proper exception handling
- Use DTOs for API requests/responses
- Follow RESTful API conventions

## Testing Guidelines
- Write unit tests using JUnit 5
- Use @SpringBootTest for integration tests
- Mock external dependencies with @MockBean
- Aim for good test coverage
- Test both positive and negative scenarios

## Common Patterns to Use
- Use @Autowired for dependency injection
- Implement validation with @Valid and validation annotations
- Use ResponseEntity for API responses
- Implement proper logging with SLF4J
- Use @Transactional for database operations
- Use Lombok annotations (@Data, @Entity, @Builder, etc.) to reduce boilerplate
- Use MapStruct for mapping between DTOs and entities
- Create mapper interfaces with @Mapper(componentModel = "spring")

## Lombok Guidelines
- Use @Data for simple POJOs (combines @Getter, @Setter, @ToString, @EqualsAndHashCode)
- Use @Entity with @Data for JPA entities
- Use @Builder for complex object creation
- Use @Slf4j for logging instead of manual logger creation
- Use @RequiredArgsConstructor for dependency injection
- Use @Value for immutable objects
- Avoid @Data on entities with bidirectional relationships (use @Getter/@Setter instead)

## MapStruct Guidelines
- Create mapper interfaces in `com.example.foods.mapper` package
- Use @Mapper(componentModel = "spring") to integrate with Spring
- Use @Mapping annotations for custom field mappings
- Handle null values with nullValueMappingStrategy
- Use @InheritInverseConfiguration for reverse mappings
- Create separate mappers for each entity-DTO pair
- Use qualifiedByName for complex mappings

## Dependencies to Consider
- Spring Boot Starter Web (already included)
- Spring Boot Starter Data JPA (for database operations)
- Spring Boot Starter Validation (for input validation)
- Spring Boot Starter Test (already included)
- H2 Database (for development/testing)
- Lombok (already included) - for reducing boilerplate code
- MapStruct (already included) - for object mapping between DTOs and entities

## File Organization
- Controllers in `com.example.foods.controller` package
- Services in `com.example.foods.service` package
- Repositories in `com.example.foods.repository` package
- DTOs in `com.example.foods.dto` package
- Entities in `com.example.foods.entity` package
- Mappers in `com.example.foods.mapper` package

## Common Tasks
When helping with this project, focus on:
- Creating CRUD operations for food items
- Implementing proper error handling
- Adding input validation
- Writing comprehensive tests
- Following Spring Boot security best practices
