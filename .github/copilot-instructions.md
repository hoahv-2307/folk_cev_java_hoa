# GitHub Copilot Instructions for Foods Application

## Project Overview
Spring Boot 4.0.1 application with Java 21 for food management featuring:
- PostgreSQL database with JPA/Hibernate
- OAuth2 (Google) + form-based authentication
- Thymeleaf templates with Bootstrap UI
- Redis session management
- RESTful APIs + web controllers
- Environment-based configuration via `.env` files

## Code Style Rules
- No comments, emojis, or icons in generated code
- Use Java 21 features when beneficial
- Follow camelCase (methods/variables), PascalCase (classes)
- Lombok for boilerplate reduction (@RequiredArgsConstructor, @Slf4j, @Data/@Builder)
- Constructor injection via @RequiredArgsConstructor (not @Autowired fields)

## Architecture & Key Patterns

### Dual Controller Pattern
This app serves both REST APIs and web pages:
- **REST**: `@RestController` at `/api/*` returning `ResponseEntity<DTO>` (see [FoodController.java](src/main/java/com/example/foods/controller/FoodController.java))
- **Web**: `@Controller` returning view names for Thymeleaf (see [WebController.java](src/main/java/com/example/foods/controller/WebController.java))

### Service Layer with Interfaces
Services follow interface + implementation pattern:
- Interface: [FoodService.java](src/main/java/com/example/foods/service/FoodService.java)
- Implementation: [FoodServiceImpl.java](src/main/java/com/example/foods/service/impl/FoodServiceImpl.java) with `@Service @Transactional`

### Entity Patterns
Entities use Lombok but with specific combinations (see [Food.java](src/main/java/com/example/foods/entity/Food.java), [User.java](src/main/java/com/example/foods/entity/User.java)):
- `@Entity @Data @Builder @NoArgsConstructor @AllArgsConstructor` for simple entities
- `@ToString(exclude = "password")` for sensitive fields
- `@PrePersist/@PreUpdate` for timestamp management
- User entity implements `UserDetails` for Spring Security integration

### MapStruct Configuration
Mappers use specific settings (see [FoodMapper.java](src/main/java/com/example/foods/mapper/FoodMapper.java)):
```java
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
```
- Always ignore `id`, `createdAt`, `updatedAt` in entity mappings
- Provide `toDtoList()` for collection conversions
- Use `updateEntityFromDto(@MappingTarget)` for updates

### Exception Handling
Centralized via [GlobalExceptionHandler.java](src/main/java/com/example/foods/exception/GlobalExceptionHandler.java):
- `@RestControllerAdvice` with `@ExceptionHandler` methods
- Returns [ErrorResponse.java](src/main/java/com/example/foods/exception/ErrorResponse.java) with timestamp, path, status
- Handles `IllegalArgumentException`, `MethodArgumentNotValidException`, generic exceptions

### Security Architecture
OAuth2 + form login hybrid (see [SecurityConfig.java](src/main/java/com/example/foods/config/SecurityConfig.java)):
- Custom [CustomOAuth2User.java](src/main/java/com/example/foods/service/CustomOAuth2User.java) wraps User entity, implements OAuth2User
- [CustomOAuth2UserService.java](src/main/java/com/example/foods/service/CustomOAuth2UserService.java) handles OAuth2 user creation/retrieval
- Role-based authorization: `/admin/**` requires `ROLE_ADMIN`
- CSRF configurable via `app.csrf.enabled` property

## Environment Configuration

### .env File System
Uses custom [DotEnvPropertyLoader.java](src/main/java/com/example/foods/config/DotEnvPropertyLoader.java) registered in [META-INF/spring.factories](src/main/resources/META-INF/spring.factories):
- Loads `.env` at application startup before Spring context
- Only sets properties not already in environment (system env takes precedence)
- Required variables: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- Optional: `GOOGLE_CLIENT_ID/SECRET`, `CSRF_ENABLED`, `LOG_LEVEL`

### Profile-Based Configuration
Three profiles with different safety levels:
- **dev** ([application-dev.properties](src/main/resources/application-dev.properties)): `ddl-auto=update`, verbose SQL logging, CSRF disabled by default
- **prod** ([application-prod.properties](src/main/resources/application-prod.properties)): `ddl-auto=validate`, minimal logging, CSRF enabled, Docker Compose disabled
- **test**: Auto-activated in tests, uses H2 in-memory database

**CRITICAL**: Never use `create-drop` outside tests - causes data loss!

## Development Workflows

### Running the Application
```bash
# Start dependencies (PostgreSQL + Redis)
docker compose up -d

# Run with dev profile (recommended for development)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests (uses H2, not PostgreSQL)
./mvnw test
```

### Build Considerations
Maven compiler plugin processes annotations in order:
1. Lombok (generates getters/setters/constructors)
2. MapStruct (uses Lombok-generated methods)
3. lombok-mapstruct-binding (ensures compatibility)

Rebuild after changing entities/DTOs: `./mvnw clean compile`

### Code Formatting
Project uses Spotify fmt-maven-plugin (Google Java Format):
- Auto-formats on compile: `./mvnw compile`
- Manual format: `./mvnw fmt:format`

## Testing Patterns
See [FoodServiceImplTest.java](src/test/java/com/example/foods/service/FoodServiceImplTest.java):
- `@ExtendWith(MockitoExtension.class)` for unit tests
- Mock dependencies with `@Mock`, inject with `@InjectMocks`
- Use AssertJ assertions: `assertThat()`, `assertThatThrownBy()`
- Test happy path + exceptions (e.g., duplicate names, not found)

## Dependencies & Integration

### Key Dependencies
- **PostgreSQL**: Primary database (H2 for tests only)
- **Redis**: Session storage (`spring-session-data-redis`)
- **Sentry**: Error monitoring (v8.27.0)
- **Actuator**: Health/metrics at `/actuator/*` (limited in prod)
- **Thymeleaf**: Server-side templates with Spring Security extras

### Package Structure
```
com.example.foods/
├── FoodsApplication.java
├── config/           # Security, data loading, .env handling
├── controller/       # REST (@RestController) + Web (@Controller)
├── dto/              # Request/response DTOs with validation
│   ├── request/
│   └── response/
├── entity/           # JPA entities
├── exception/        # Global exception handling
├── mapper/           # MapStruct interfaces
├── repository/       # Spring Data JPA repositories
└── service/          # Business logic (interface + impl/)
```

## Common Anti-Patterns to Avoid
- Don't use `@Data` on User entity (has `@ToString(exclude = "password")` for security)
- Don't skip `@Transactional(readOnly = true)` on query-only service methods
- Don't hardcode OAuth2 credentials (use .env file)
- Don't expose `User` entity directly - use DTOs or custom OAuth2User wrapper
- Don't forget MapStruct's annotation processor path in pom.xml
