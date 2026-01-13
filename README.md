# Foods Application

A Spring Boot application for managing food-related operations with web interface and PostgreSQL database.

## Table of Contents
- [About](#about)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Environment Configuration](#environment-configuration)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Docker Support](#docker-support)
- [CI/CD Pipeline](#cicd-pipeline)
- [Project Structure](#project-structure)

## About

This is a Spring Boot application built for food management operations. The project includes:
- Thymeleaf-based web interface with Bootstrap styling
- PostgreSQL database integration
- User authentication and authorization
- RESTful APIs for food management
- Docker support for easy deployment
- Environment-based configuration

## Technologies Used

- **Java 21** - Programming language
- **Spring Boot 4.0.1** - Main framework
- **PostgreSQL** - Primary database
- **Thymeleaf** - Server-side template engine
- **Bootstrap 5** - Frontend styling framework
- **Maven** - Build tool and dependency management
- **Lombok** - Reduces boilerplate code with annotations
- **MapStruct** - Object mapping between DTOs and entities
- **Docker & Docker Compose** - Containerization

## Prerequisites

Before running this application, make sure you have the following installed:

- Java 21 or higher
- Maven 3.6+ (or use the included Maven wrapper)
- Docker and Docker Compose (for database services)

## Environment Configuration

This application uses environment variables for sensitive configuration. Follow these steps:

1. **Copy the environment template**
   ```bash
   cp .env.example .env
   ```

2. **Edit the .env file** with your actual values:
   ```bash
   # Database Configuration (matches compose.yaml)
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=mydatabase
   DB_USERNAME=myuser
   DB_PASSWORD=secret
   
   # OAuth2 Configuration (optional)
   GOOGLE_CLIENT_ID=your_google_client_id_here
   GOOGLE_CLIENT_SECRET=your_google_client_secret_here
   ```

3. **Available Environment Variables**:
   - DB_* - Database connection settings
   - GOOGLE_CLIENT_* - Google OAuth2 credentials (set to 'dummy' to disable)
   - JWT_SECRET - Secret key for JWT tokens
   - MAIL_* - Email configuration for notifications
   - REDIS_* - Redis configuration
   - LOG_LEVEL - Application logging level

**Important**: Never commit the .env file to version control. It's included in .gitignore.

### Database DDL Settings Explained

- **`validate`** - Hibernate validates the schema matches entities but makes no changes
- **`update`** - Hibernate updates the schema to match entities, preserves existing data
- **`create-drop`** - Hibernate drops and recreates all tables on startup (DATA LOSS!)

**CRITICAL**: Never use `create-drop` in production or with real data!

## Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd cev_java_hoa
   ```

2. **Set up environment configuration**
   ```bash
   cp .env.example .env
   # Edit .env with your actual values
   ```

3. **Start the database services**
   ```bash
   docker compose up -d
   ```

4. **Build the project**
   ```bash
   ./mvnw clean install
   ```
   
   Or on Windows:
   ```cmd
   mvnw.cmd clean install
   ```

### Using Maven Directly

```bash
# Start database services first
docker compose up -d

# Run with development profile (recommended)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run with production profile (default behavior)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Docker Commands

**Local Development:**
```bash
# Build image
docker build -t foods-app .

# Run with Docker Compose
docker-compose up -d
```
## Testing

Run the test suite using:

```bash
./mvnw test
```

## Docker Support

The project includes Docker support via compose.yaml. To run with Docker:

```bash
docker-compose up --build
```

## Project Structure

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

**Note**: This README will be updated as the project evolves. Please check back for the latest information.