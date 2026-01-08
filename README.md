# Foods Application

A Spring Boot application for managing food-related operations with web interface and PostgreSQL database.

## Table of Contents
- [About](#about)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Environment Configuration](#environment-configuration)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Docker Support](#docker-support)
- [GitHub Copilot Instructions](#github-copilot-instructions)
- [Contributing](#contributing)

## About

This is a Spring Boot application built for food management operations. The project includes:
- Beautiful Thymeleaf-based web interface with Bootstrap styling
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
   - `DB_*` - Database connection settings
   - `GOOGLE_CLIENT_*` - Google OAuth2 credentials (set to 'dummy' to disable)
   - `JWT_SECRET` - Secret key for JWT tokens
   - `MAIL_*` - Email configuration for notifications
   - `REDIS_*` - Redis configuration
   - `LOG_LEVEL` - Application logging level

**Important**: Never commit the `.env` file to version control. It's included in `.gitignore`.

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

## Running the Application

### Using Maven (Recommended)

```bash
# Start database services first
docker compose up -d

# Run the application
./mvnw spring-boot:run
```

The application will be available at: http://localhost:8080

### Using Java

```bash
./mvnw clean package
java -jar target/foods-0.0.1-SNAPSHOT.jar
```

### Demo Users

The application comes with pre-loaded demo users:
- **Admin**: username=`admin`, password=`admin123`
- **User**: username=`user`, password=`user123`

## Web Interface

The application provides a modern web interface built with Thymeleaf and Bootstrap:

### Login & Registration
- **Login Page**: `/login` - Clean, responsive login form
- **Registration**: `/register` - User registration form
- **Authentication**: Form-based login with session management

### Main Dashboard
- **Foods List**: `/foods` - Comprehensive food management interface
- **Search & Filter**: Search by name, filter by category and price range
- **CRUD Operations**: Add, edit, delete food items
- **Responsive Design**: Mobile-friendly interface

### Features
- Real-time search functionality
- Category-based filtering
- Price range filtering
- Pagination support
- Modern Bootstrap 5 styling
- Accessible UI components

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