# Foods App - Copilot Instructions

## Tech Stack
- **Java 21** + **Spring Boot 4.0.1**
- **PostgreSQL** + **Redis** + **MinIO/S3**
- **Thymeleaf** + **Bootstrap** + **OAuth2**

## Code Style
- No comments/emojis in code
- Use Lombok: `@RequiredArgsConstructor`, `@Slf4j`, `@Data/@Builder`
- Constructor injection (not `@Autowired` fields)
- Java 21 features when helpful

## Key Patterns

### Controllers
- **REST APIs**: `@RestController` at `/api/*` → return `ResponseEntity<DTO>`
- **Web Pages**: `@Controller` → return view names for Thymeleaf

### Services
- Interface + `@Service @Transactional` implementation
- Use `@Transactional(readOnly = true)` for queries

### Entities
- `@Entity @Data @Builder @NoArgsConstructor @AllArgsConstructor`
- `@PrePersist/@PreUpdate` for timestamps
- User: `@ToString(exclude = "password")` for security

### MapStruct
```java
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = IGNORE)
```
- Ignore `id`, `createdAt`, `updatedAt` in mappings
- Provide `toDtoList()` and `updateEntityFromDto(@MappingTarget)`

### Images
- Upload to MinIO via `FileStorageService.uploadFile()`
- Serve via `/api/foods/images/{filename}`
- Food entity has `List<FoodImage> foodImages`

## Environment
- Use `.env` file for config (never commit)
- **dev** profile: `ddl-auto=update`, detailed logs
- **prod** profile: `ddl-auto=validate`, minimal logs
- Never use `create-drop` (data loss!)

## Common Mistakes
- Don't use `@Data` on User (use `@ToString(exclude = "password")`)
- Don't expose entities directly (use DTOs)
- Don't forget `@Transactional(readOnly = true)`
- Always use constructor injection
