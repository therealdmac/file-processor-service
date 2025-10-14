# File Uploader Application

This project is a **full-stack file uploader** application with a PostgreSQL database backend, a Java backend service, and an Angular frontend. It can be run **natively** or using **Docker** for easy deployment.

---

## Quick Start

Get the app running quickly using Docker:

1. Make sure you have **Docker** and **Docker Compose** installed.
2. Copy the example `.env` file and adjust if needed:

```bash
cp .env.example .env
```

3. Start all services with one command:

```bash
docker-compose --env-file .env up --build
```

4. Open your browser to access the services:

- Frontend: `http://localhost:4200`  
- Backend API: `http://localhost:8080`  
- Adminer (DB Admin): `http://localhost:8081`

5. To stop the application:

```bash
docker-compose down
```

✅ Everything runs fully containerized, ready for development or production.

---

## Table of Contents

- [Prerequisites](#prerequisites)  
- [Environment Variables](#environment-variables)  
- [Running Natively](#running-natively)  
- [Running with Docker](#running-with-docker)  
- [Ports](#ports)  
- [Notes](#notes)

---

## Prerequisites

Before running the application natively, ensure you have the following installed:

- **Java 11+** (for backend)
- **Maven** (for backend build)
- **Node.js 18+** (for frontend)
- **Angular CLI** (for frontend)
- **PostgreSQL 16+** (for local database)
- Optional: **Docker & Docker Compose** (for containerized setup)

---

## Environment Variables

Create a `.env` file in the project root with the following:

```dotenv
# Database configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=filedb
DB_HOST=db
DB_PORT=5432
DB_NAME=filedb
DB_USER=postgres
DB_PASS=postgres

# Ports
DB_PORT_LOCAL=54322
BACKEND_PORT=8080
ADMINER_PORT=8081
FRONTEND_PORT=4200
```

These variables are used both **natively** and in **Docker**.

---

## Running Natively

### 1. Start PostgreSQL

Create a database:

```bash
createdb -U postgres filedb
```

Or adjust the credentials to match your `.env` file.

### 2. Run Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 3. Run Frontend

```bash
cd file-processor-ui
npm install
ng serve --port 4200
```

Frontend will be available at: `http://localhost:4200`

---

## Running with Docker

### 1. Build and start services

```bash
docker-compose --env-file .env up --build
```

This will start:

- **PostgreSQL** (`db`)  
- **Backend** (`fileuploader-backend`)  
- **Frontend** (`file-frontend`)  
- **Adminer** (`fileuploader-db-postgres-admin`)  

### 2. Access Services

| Service | URL |
|---------|-----|
| Frontend | `http://localhost:4200` |
| Backend API | `http://localhost:8080` |
| Adminer (DB Admin) | `http://localhost:8081` |

### 3. Stop Services

```bash
docker-compose down
```

---

## Ports

| Service | Default Port | Configurable via `.env` |
|---------|--------------|------------------------|
| PostgreSQL | 5432 | DB_PORT_LOCAL |
| Backend | 8080 | BACKEND_PORT |
| Adminer | 8080 | ADMINER_PORT |
| Frontend | 80 | FRONTEND_PORT |

---

