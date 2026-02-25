# IQB Interview POC

A web application for managing students, courses, and exam results.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3, Spring Data JPA |
| Frontend | Angular 21 (zoneless), Angular Material |
| Database | SQLite |
| Build | Maven (backend), pnpm (frontend) |
| CI | GitHub Actions |

## Prerequisites

- **Java** 21 LTS (`brew install openjdk@21`)
- **Node.js** 22 LTS (`nvm use 22`)
- **pnpm** (`npm i -g pnpm`)
- **Maven** (`brew install maven`)

## Getting Started

```bash
# Install frontend dependencies
pnpm install

# Start both backend and frontend
pnpm dev
```

This runs the Spring Boot server on `http://localhost:8080` and the Angular dev server on `http://localhost:4200`.

### Running individually

```bash
# Backend only
mvn spring-boot:run

# Frontend only
pnpm start
```

## Features

### Dashboard
- Total students, courses, and exam results at a glance
- Average score, completed/in-progress course pairs
- Top 5 students by average score
- Recent exam results
- Score distribution chart

### Students
- Paginated, searchable student list (name, number, email, phone)
- Completed course average chips per student
- Student detail page with all exam results and per-course averages
- Create, edit, and delete students via dialog

### Courses
- Paginated course list with create, edit, and delete

### Exam Scores
- Paginated, searchable exam result list
- Add/edit scores per student — up to 3 scores per course
- Unsaved changes guard prevents accidental navigation

## Business Rules

- A course is **completed** when a student has exactly 3 scores for it
- Maximum 3 exam scores per student-course pair (enforced on backend)
- Score range: 0-100

## Project Structure

```
src/
├── client/                  # Angular frontend
│   ├── models/              # TypeScript interfaces
│   ├── pages/               # Page components (dashboard, students, courses, exam-results)
│   ├── services/            # HTTP services
│   ├── guards/              # Route guards
│   └── app.routes.ts        # Lazy-loaded routes
├── server/                  # Spring Boot backend
│   └── java/com/iqb/interviewpoc/
│       ├── controller/      # REST controllers
│       ├── service/         # Business logic
│       ├── repository/      # JPA repositories
│       ├── entity/          # JPA entities
│       ├── dto/             # Data transfer objects
│       ├── exception/       # Custom exceptions
│       └── config/          # Global exception handler, Swagger
└── test/                    # Integration tests
```

## Database Schema

| Table | Columns |
|-------|---------|
| student | id, full_name, number, email, gsm_number |
| course | id, name |
| exam_result | id, student_id (FK), course_id (FK), score |

## Scripts

| Command | Description |
|---------|-------------|
| `pnpm dev` | Start backend + frontend |
| `pnpm build` | Production build (output to `src/server/resources/static`) |
| `pnpm lint` | Lint frontend |
| `pnpm format` | Format frontend with Prettier |
| `mvn test` | Run backend tests |

## API Documentation

Swagger UI is available at `http://localhost:8080/swagger-ui.html` when the backend is running.
