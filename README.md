# Habit Tracker API

Habit Tracker API is a backend application for tracking daily habits, habit completions, and habit statistics.

The project is built with Java, Spring Boot, PostgreSQL, and Docker.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Docker
- Maven
- Lombok
- Bean Validation

## Features

- Create habits
- Get active habits
- Get habit by id
- Update habit
- Archive habit
- Create daily habit check-ins
- Get habit check-ins
- Delete habit check-ins
- Prevent duplicate check-ins for the same day
- Calculate total check-ins
- Calculate current streak
- Calculate longest streak
- Calculate weekly completion statistics
- Basic API error handling
- Docker-based local setup

## Running with Docker

Make sure Docker is installed and running.

From the project root, run:

```bash
docker compose up --build
```

The application will be available at:

```text
http://localhost:8080
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

## API Endpoints

### Habits

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/habits` | Create habit |
| GET | `/api/habits` | Get active habits |
| GET | `/api/habits/{id}` | Get habit by id |
| PUT | `/api/habits/{id}` | Update habit |
| DELETE | `/api/habits/{id}` | Archive habit |
| GET | `/api/habits/{id}/stats` | Get habit statistics |

### Check-ins

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/habits/{habitId}/check-ins` | Create check-in for today |
| GET | `/api/habits/{habitId}/check-ins` | Get habit check-ins |
| DELETE | `/api/habits/{habitId}/check-ins/{checkInId}` | Delete check-in |

## Request Examples

### Create Habit

```http
POST /api/habits
Content-Type: application/json

{
  "title": "Read 20 pages",
  "description": "Read a book every evening"
}
```

### Update Habit

```http
PUT /api/habits/1
Content-Type: application/json

{
  "title": "Read 30 pages",
  "description": "Read before sleep"
}
```

### Create Check-in

```http
POST /api/habits/1/check-ins
```

### Get Habit Stats

```http
GET /api/habits/1/stats
```

Example response:

```json
{
  "habitId": 1,
  "totalCheckIns": 10,
  "currentStreak": 3,
  "longestStreak": 5,
  "completedDaysLast7Days": 4,
  "completionRateLast7Days": 57.14
}
```

## Error Response Example

```json
{
  "status": 404,
  "message": "Habit with id 999 not found",
  "timestamp": "2026-04-29T14:30:00"
}
```

## Future Improvements

- Add users and authentication
- Add JWT authorization
- Add unit and integration tests
- Add Swagger/OpenAPI documentation
- Add monthly statistics
- Add habit frequency settings
- Add production-ready database migrations