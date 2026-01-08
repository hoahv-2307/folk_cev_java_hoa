# Foods Application

A Spring Boot application for managing food-related operations.

## Table of Contents
- [About](#about)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Docker Support](#docker-support)
- [GitHub Copilot Instructions](#github-copilot-instructions)
- [Contributing](#contributing)

## About

This is a Spring Boot application built for food management operations. The project follows modern Spring Boot practices and includes Docker support for easy deployment.

## Technologies Used

- **Java 21** - Programming language
- **Spring Boot 4.0.1** - Main framework
- **Maven** - Build tool and dependency management
- **Lombok** - Reduces boilerplate code with annotations
- **MapStruct** - Object mapping between DTOs and entities
- **Docker** - Containerization (see compose.yaml)

## Prerequisites

Before running this application, make sure you have the following installed:

- Java 21 or higher
- Maven 3.6+ (or use the included Maven wrapper)
- Docker and Docker Compose (optional, for containerized deployment)

## Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd cev_java_hoa
   ```

2. **Build the project**
   ```bash
   ./mvnw clean install
   ```
   
   Or on Windows:
   ```cmd
   mvnw.cmd clean install
   ```

## Running the Application

### Using Maven

```bash
./mvnw spring-boot:run
```

### Using Java

```bash
./mvnw clean package
java -jar target/foods-0.0.1-SNAPSHOT.jar
```

### Using Docker Compose

```bash
docker-compose up
```

The application will start on `http://localhost:8080` by default.

## API Documentation

Once the application is running, you can access:

- **Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health (if actuator is configured)

## Testing

Run the test suite using:

```bash
./mvnw test
```

## Docker Support

The project includes Docker support via `compose.yaml`. To run with Docker:

```bash
docker-compose up --build
```

## GitHub Copilot Instructions

This project includes GitHub Copilot instructions to help with consistent code generation and best practices. The instructions are located in `.github/copilot-instructions.md` and cover:

- Project-specific coding standards
- Architecture patterns to follow
- Testing guidelines
- Common dependencies and packages
- File organization structure

These instructions help Copilot provide more relevant suggestions tailored to this Spring Boot food management application.

## Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Project Structure

```
src/
├── main/
│   ├── java/com/example/foods/
│   │   ├── FoodsApplication.java
│   │   ├── entity/
│   │   │   └── Food.java (example entity with Lombok annotations)
│   │   ├── dto/
│   │   │   └── FoodDto.java (example DTO with validation)
│   │   └── mapper/
│   │       └── FoodMapper.java (MapStruct mapper interface)
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/foods/
        └── FoodsApplicationTests.java
```

---

**Note**: This README will be updated as the project evolves. Please check back for the latest information.