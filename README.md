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

## CI/CD Pipeline

This project includes a comprehensive CI/CD pipeline using GitHub Actions with the following features:

### Continuous Integration (.github/workflows/ci.yml)

**Automated Testing:**
- Unit and integration tests with PostgreSQL and Redis services
- Test coverage reporting and artifact upload
- Multi-environment test configuration

**Code Quality:**
- Maven Checkstyle validation
- SpotBugs security analysis
- SonarQube integration (configurable)

**Security Scanning:**
- Trivy vulnerability scanner
- SARIF upload to GitHub Security tab

**Build & Package:**
- Maven build with dependency caching
- JAR artifact generation and upload
- Docker image build and push to registry

### Continuous Deployment (.github/workflows/deploy.yml)

**Deployment Features:**
- Tag-based production deployments (v*.*.*)
- Manual workflow dispatch with environment selection
- Zero-downtime deployment strategy
- Automated rollback on failure
- Health check validation

**Infrastructure:**
- Docker containerization with multi-stage builds
- Production-ready Docker Compose configuration
- NGINX reverse proxy with rate limiting
- SSL/TLS support (configurable)

### Monitoring & Health Checks

**Built-in Monitoring:**
- Spring Boot Actuator endpoints (/actuator/health, /actuator/metrics)
- Docker health checks with automatic restart
- NGINX health monitoring
- Database and Redis connection monitoring

### Setup Instructions

**1. Repository Secrets Configuration**

Add these secrets to your GitHub repository settings:

```bash
# Docker Registry
DOCKER_USERNAME=your-docker-username
DOCKER_PASSWORD=your-docker-password

# Database Configuration
DB_HOST=your-db-host
DB_PORT=5432
DB_NAME=your-database-name
DB_USERNAME=your-db-username
DB_PASSWORD=your-db-password

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Security
JWT_SECRET=your-jwt-secret-key-32-characters-minimum
CSRF_ENABLED=true

# Deployment Server
DEPLOY_HOST=your-server-ip-or-domain
DEPLOY_USER=your-server-username
DEPLOY_SSH_KEY=your-private-ssh-key
DEPLOY_PORT=22

# Optional: Email & Redis
MAIL_PASSWORD=your-email-password
REDIS_PASSWORD=your-redis-password
```

**2. Server Preparation**

Prepare your deployment server:

```bash
# Install Docker and Docker Compose
sudo apt update && sudo apt install -y docker.io docker-compose

# Create deployment directory
sudo mkdir -p /opt/foods-app
sudo chown $USER:$USER /opt/foods-app

# Copy production files
scp docker-compose.prod.yml nginx.conf user@server:/opt/foods-app/
```

**3. Deployment Process**

- **Automatic**: Push tags like v1.0.0 for production deployment
- **Manual**: Use GitHub Actions "Deploy to Production" workflow
- **Monitoring**: Check /actuator/health endpoint for application status

### Docker Commands

**Local Development:**
```bash
# Build image
docker build -t foods-app .

# Run with Docker Compose
docker-compose up -d
```

**Production Deployment:**
```bash
# Start production stack
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.prod.yml logs -f foods-app

# Scale application
docker-compose -f docker-compose.prod.yml up -d --scale foods-app=3
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

**Note**: This README will be updated as the project evolves. Please check back for the latest information.