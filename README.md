# Foods Application

A Spring Boot application for food management with image support, user authentication, and admin controls.

## Features

- 🍕 **Food Management**: Create, view, edit, and delete food items
- 🖼️ **Image Support**: Upload and manage multiple images per food item  
- 👤 **User Authentication**: Google OAuth2 + form-based login
- 🛡️ **Admin Controls**: Admin dashboard for food management
- 📱 **Responsive UI**: Bootstrap-based interface for all devices
- 🏪 **File Storage**: MinIO/S3 integration for image storage

## Technologies

- **Java 21** + **Spring Boot 4.0.1**
- **PostgreSQL** (database) + **Redis** (sessions)
- **MinIO** (file storage) + **Thymeleaf** (templates)
- **OAuth2** (authentication) + **Bootstrap 5** (UI)
- **Docker Compose** (local development)

## Quick Start

### 1. Setup Environment
```bash
# Copy environment template
cp .env.example .env

# Edit .env with your values:
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mydatabase
DB_USERNAME=myuser
DB_PASSWORD=secret

# For image storage (MinIO)
AWS_S3_STORAGE_ENDPOINT=http://localhost:9000
S3_BUCKET_NAME=food-images
S3_ACCESS_KEY=minioadmin
S3_SECRET_KEY=minioadmin
```

### 2. Start Services
```bash
# Start database, Redis, and MinIO
docker compose up -d
```

### 3. Run Application
```bash
# Development mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Access the application
# User Interface: http://localhost:8080
# MinIO Console: http://localhost:9001
```

### 4. Default Accounts
- **Admin**: Create via registration, then update role in database
- **User**: Register at `/register` or login with Google OAuth2

## Food Image Management

### For Users
- Browse foods with images in card layout
- Click images/names to view detailed pages
- Add foods to cart from list or detail views

### For Admins  
- Upload multiple images when creating foods
- View current images when editing foods
- Replace all images or keep existing ones
- Images automatically resize and optimize

### File Storage
- Images stored in MinIO (S3-compatible)
- Automatic file naming and organization
- Supports JPG, PNG, GIF formats
## Development

### Run Tests
```bash
./mvnw test
```

### Build Project
```bash
./mvnw clean install
```

### Profiles
- **dev**: Development mode with detailed logging
- **prod**: Production mode with optimized settings
- **test**: Testing with H2 in-memory database

## API Endpoints

### Food Management
- `GET /api/foods` - List all foods
- `POST /api/foods` - Create food (with images)
- `PUT /api/foods/{id}` - Update food (with images)
- `DELETE /api/foods/{id}` - Delete food
- `GET /api/foods/images/{filename}` - Serve food images

### Web Pages
- `/` - Food listing (user/admin views)
- `/foods/{id}` - Food detail page
- `/login` - Authentication page
- `/profile` - User profile and orders