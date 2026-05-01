# Habit Tracker API

Habit Tracker API is a backend application for tracking habits, daily completions, user-specific habit progress, and habit statistics.

The project is built with Java, Spring Boot, PostgreSQL, Docker, JWT authentication, and Swagger/OpenAPI documentation.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Docker
- Docker Compose
- Maven
- Lombok
- Bean Validation
- JWT
- Swagger / OpenAPI

## Features

- User registration
- User login
- JWT token generation
- JWT-protected API endpoints
- Get current authenticated user
- Create habits
- Get active habits of the current user
- Get habit by id
- Update habit
- Archive habit
- Get archived habits
- Restore archived habits
- Create daily habit check-ins
- Get habit check-ins
- Delete habit check-ins
- Prevent duplicate check-ins for the same day
- Restrict habits and check-ins to the habit owner
- Calculate total check-ins
- Calculate current streak
- Calculate longest streak
- Calculate weekly completion statistics
- Show whether the habit is completed today
- Dashboard summary endpoint for frontend
- Basic API error handling
- Docker-based local setup
- Swagger UI for API testing

## Running with Docker

Make sure Docker Desktop is installed and running.

From the project root, run:

```bash
docker compose up --build
```

The application will be available at:

```text
http://localhost:8080
```

Swagger UI will be available at:

```text
http://localhost:8080/swagger-ui.html
```

PostgreSQL will be available on the host machine at:

```text
localhost:5433
```

To stop containers:

```bash
docker compose down
```

To stop containers and remove database data:

```bash
docker compose down -v
```

If the backend code was updated, run:

```bash
git pull
docker compose up --build
```

## Running Locally

Create a local PostgreSQL database:

```sql
CREATE DATABASE habit_tracker;
```

Create local config file:

```text
src/main/resources/application-local.yml
```

Example:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/habit_tracker
    username: postgres
    password: your_password
    driver-class-name: org.postgresql.Driver
```

Run the application:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw spring-boot:run
```

## Authentication

Most API endpoints are protected by JWT authentication.

Public endpoints:

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user and receive JWT token |

Protected endpoints require this header:

```http
Authorization: Bearer <token>
```

Example:

```http
GET /api/habits
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## API Endpoints

### Auth

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |

### Users

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users/me` | Get current authenticated user |

### Habits

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/habits` | Create habit |
| GET | `/api/habits` | Get active habits of current user |
| GET | `/api/habits/summary` | Get dashboard habit summaries |
| GET | `/api/habits/archived` | Get archived habits |
| GET | `/api/habits/{id}` | Get habit by id |
| PUT | `/api/habits/{id}` | Update habit |
| DELETE | `/api/habits/{id}` | Archive habit |
| PATCH | `/api/habits/{id}/restore` | Restore archived habit |
| GET | `/api/habits/{id}/stats` | Get habit statistics |

### Check-ins

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/habits/{habitId}/check-ins` | Create check-in for today |
| GET | `/api/habits/{habitId}/check-ins` | Get habit check-ins |
| DELETE | `/api/habits/{habitId}/check-ins/{checkInId}` | Delete check-in |

## Request Examples

### Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "jake",
  "email": "jake@example.com",
  "password": "123456"
}
```

Example response:

```json
{
  "userId": 1,
  "username": "jake",
  "email": "jake@example.com",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "jake@example.com",
  "password": "123456"
}
```

Example response:

```json
{
  "userId": 1,
  "username": "jake",
  "email": "jake@example.com",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Get Current User

```http
GET /api/users/me
Authorization: Bearer <token>
```

Example response:

```json
{
  "id": 1,
  "username": "jake",
  "email": "jake@example.com"
}
```

### Create Habit

```http
POST /api/habits
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Read 20 pages",
  "description": "Read a book every evening"
}
```

### Get Active Habits

```http
GET /api/habits
Authorization: Bearer <token>
```

### Get Dashboard Summary

```http
GET /api/habits/summary
Authorization: Bearer <token>
```

Example response:

```json
[
  {
    "id": 1,
    "title": "Read 20 pages",
    "description": "Read a book every evening",
    "createdAt": "2026-05-01T18:30:00",
    "completedToday": true,
    "currentStreak": 3,
    "longestStreak": 5,
    "completedDaysLast7Days": 4,
    "completionRateLast7Days": 57.14
  }
]
```

### Update Habit

```http
PUT /api/habits/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Read 30 pages",
  "description": "Read before sleep"
}
```

### Archive Habit

```http
DELETE /api/habits/1
Authorization: Bearer <token>
```

### Get Archived Habits

```http
GET /api/habits/archived
Authorization: Bearer <token>
```

### Restore Habit

```http
PATCH /api/habits/1/restore
Authorization: Bearer <token>
```

### Create Check-in

```http
POST /api/habits/1/check-ins
Authorization: Bearer <token>
```

A check-in means that the user completed the habit today.

Only one check-in per habit is allowed per day.

### Get Habit Check-ins

```http
GET /api/habits/1/check-ins
Authorization: Bearer <token>
```

### Delete Check-in

```http
DELETE /api/habits/1/check-ins/5
Authorization: Bearer <token>
```

### Get Habit Stats

```http
GET /api/habits/1/stats
Authorization: Bearer <token>
```

Example response:

```json
{
  "habitId": 1,
  "totalCheckIns": 10,
  "currentStreak": 3,
  "longestStreak": 5,
  "completedDaysLast7Days": 4,
  "completionRateLast7Days": 57.14,
  "completedToday": true
}
```

## Main User Flow

1. User registers or logs in.
2. Backend returns JWT token.
3. Frontend saves token.
4. Frontend sends token in `Authorization` header.
5. User creates habits.
6. User marks habits as completed by creating check-ins.
7. Backend calculates statistics from check-ins.
8. User can archive or restore habits.

## Important Frontend Notes

Frontend should use this base URL:

```text
http://localhost:8080/api
```

Frontend must include JWT token for protected requests:

```http
Authorization: Bearer <token>
```

For dashboard page, frontend should use:

```http
GET /api/habits/summary
```

This endpoint already returns the main data needed for habit cards:

- habit id
- title
- description
- creation date
- completed today status
- current streak
- longest streak
- completed days during last 7 days
- weekly completion rate

For login persistence, frontend can call:

```http
GET /api/users/me
```

If the token is valid, backend returns the current user.

## Error Response Example

```json
{
  "status": 404,
  "message": "Habit with id 999 not found",
  "timestamp": "2026-05-01T18:30:00"
}
```

## Common Error Cases

### Invalid validation data

Example: empty title while creating habit.

Expected status:

```text
400 Bad Request
```

### Habit not found

Example: user requests non-existing habit.

Expected status:

```text
404 Not Found
```

### Access denied

Example: user tries to access another user's habit.

Expected status:

```text
403 Forbidden
```

### Duplicate check-in

Example: user tries to check in the same habit twice in one day.

Expected status:

```text
409 Conflict
```

## Swagger

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

Use Swagger to test API endpoints directly in the browser.

## Future Improvements

- Add unit and integration tests
- Add refresh tokens
- Add logout/token invalidation
- Add monthly statistics
- Add habit frequency settings
- Add production-ready database migrations
- Add role-based authorization if needed