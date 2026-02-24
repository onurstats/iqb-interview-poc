# Engineering Review: IQB Interview POC

**Reviewer:** AI Engineering Lead
**Date:** 2026-02-24
**Submission:** Full-stack Student/Course/Exam Management Application
**Stack:** Angular 21 + Spring Boot 3.5 + SQLite

---

## Executive Summary

This is a well-structured full-stack POC that demonstrates solid engineering fundamentals. The candidate built a complete student management system with CRUD operations, exam score tracking, a statistics dashboard, and API documentation — all within a clean monorepo layout. The code is readable, the architecture is sensible, and the tooling choices are modern.

**Recommendation: HIRE** — The candidate shows strong full-stack capability, good architectural instincts, and attention to developer experience. Weaknesses are mostly about production-readiness concerns that are acceptable in a POC context.

---

## Strengths (Why Hire)

### 1. Clean Architecture & Project Structure
- Unified monorepo with clear separation: `src/client/` (Angular) and `src/server/` (Spring Boot)
- Angular production build outputs to `src/server/resources/static/` — single JAR deployment in production
- Schema-first database design (`schema.sql`) instead of relying on Hibernate auto-DDL — shows discipline
- Gitflow branching model (`main` → `develop` → `feature/*`) — understands team workflows

### 2. Modern Tech Stack Choices
- **Angular 21** with standalone components (no NgModules), zoneless change detection, and `inject()` function — latest patterns, not legacy
- **Angular Material** for consistent UI — practical choice over custom CSS
- **Spring Boot 3.5** with Java 21 records for DTOs — concise, immutable data carriers
- **SQLite** for a POC — pragmatic, zero-config, no external DB dependency to run the project

### 3. Database Design
- Proper foreign keys with `ON DELETE CASCADE` — data integrity enforced at DB level
- `CHECK (score >= 0 AND score <= 100)` constraint — validation at the lowest level
- Composite index on `(student_id, course_id)` — performance awareness
- Idempotent schema (`IF NOT EXISTS`) — safe to re-run
- Separate indexes for search fields (full_name, email, number, course name)
- PRAGMA foreign_keys enabled per-connection via Hikari init SQL — knows SQLite quirks

### 4. API Design
- RESTful endpoints with proper HTTP methods and status codes (201 Created, 204 No Content, 404 Not Found)
- Server-side pagination with Spring Data `Pageable` on all list endpoints
- Search/filter support on all list endpoints with case-insensitive matching
- OpenAPI 3.1 documentation with Swagger UI — API is self-documenting
- Clean DTOs separate internal entities from API responses (ExamResultDto, StudentScoresDto, DashboardStatsDto)

### 5. Frontend Quality
- Reactive search with `Subject` + `debounceTime(300ms)` + `distinctUntilChanged()` — no excessive API calls
- `MatTableDataSource` for table binding in zoneless Angular — avoids NG0100 errors
- `ChangeDetectorRef.markForCheck()` in all async callbacks — understands zoneless change detection
- Color-coded score badges (green/orange/red) — good UX attention
- Responsive CSS grid with media queries on dashboard — mobile consideration
- Course autocomplete with filtered available options — thoughtful UX

### 6. Domain Modeling
- 3-score completion rule (student-course pair is "completed" when 3 scores exist) — correctly modeled
- Score entry form handles create/update/delete in a single save operation — complex state management done well
- Dashboard aggregations (averages, distributions, top performers) — shows data thinking

### 7. Tooling & Developer Experience
- ESLint with `angular-eslint` (recommended + template accessibility + stylistic rules)
- Prettier integrated via `eslint-plugin-prettier` — formatting is a lint error, not a separate step
- `pnpm lint`, `pnpm format`, `pnpm format:check` scripts — CI-ready
- EditorConfig for consistent editor settings
- OpenAPI/Swagger UI at `/swagger-ui/index.html` — instant API exploration

---

## Weaknesses (Concerns)

### 1. No Service Layer (HIGH)
Controllers directly inject repositories and contain all business logic. This works for a POC but is a code smell at scale:
- Business rules (3-score max, score validation, cascade operations) live in controller methods
- No place for shared business logic if multiple controllers need the same operations
- Harder to unit test business logic in isolation

**Files affected:** All controllers (`StudentController`, `CourseController`, `ExamResultController`, `DashboardController`)

### 2. No Authentication or Authorization (MEDIUM)
- All endpoints are publicly accessible
- No user concept, no roles, no JWT/session handling
- CORS allows `localhost:4200` and `localhost:8080` — appropriate for dev but no production CORS strategy

**Acceptable for POC scope, but should be acknowledged.**

### 3. Hardcoded Base URLs in Frontend (MEDIUM)
All services use `http://localhost:8080` as the base URL:
- `student.service.ts` → `http://localhost:8080/api/students`
- `course.service.ts` → `http://localhost:8080/api/courses`
- etc.

Should use `environment.ts` or Angular's `APP_BASE_HREF` / proxy config for environment-specific URLs.

### 4. Insufficient Test Coverage (HIGH)
- Only 10 database migration tests exist (`DatabaseMigrationTest`)
- No controller/integration tests
- No frontend unit tests (Angular schematics configured to skip test generation)
- No e2e tests

This is the biggest gap — a candidate who writes tests demonstrates engineering maturity.

### 5. No Global Exception Handling (MEDIUM)
- No `@ControllerAdvice` or `@ExceptionHandler` for consistent error responses
- If a database constraint violation occurs, the client gets a raw 500 with stack trace
- No structured error response format (e.g., `{ error: string, message: string, status: number }`)

### 6. Configuration Issues (LOW)
- `spring.jpa.show-sql=true` — should be off or profile-specific
- `spring.sql.init.mode=always` — runs `data.sql` seed on every startup, causes test DB conflicts
- No Spring profiles (`dev`, `prod`) for environment-specific config

### 7. No Request Body Validation on DTOs (LOW)
- `SaveScoresRequest` record has no `@Valid` or `@NotNull` annotations
- A malformed request body would result in unclear errors
- Entity-level validation exists (`@Min`, `@Max`, `@NotBlank`) but DTO-level is missing

---

## Improvement Suggestions

| # | Improvement | Priority | What it Demonstrates |
|---|------------|----------|---------------------|
| 1 | Add a service layer between controllers and repositories | High | Separation of concerns, testability |
| 2 | Write controller integration tests with `@WebMvcTest` or `@SpringBootTest` | High | Testing discipline, confidence in changes |
| 3 | Add `@ControllerAdvice` for global exception handling | High | Production readiness, API consistency |
| 4 | Extract base URLs to `environment.ts` | Medium | Environment awareness, deployment readiness |
| 5 | Add Spring profiles (`dev`, `test`, `prod`) | Medium | Configuration management |
| 6 | Add `@Valid` annotations on request body DTOs | Medium | Defense in depth, input validation |
| 7 | Disable `show-sql` in non-dev profiles | Low | Production hygiene |
| 8 | Add Angular unit tests for at least services and key components | Medium | Frontend testing culture |
| 9 | Add a CI pipeline (GitHub Actions) with lint + test + build | Medium | DevOps maturity |
| 10 | Consider replacing SQLite with PostgreSQL for production path | Low | Scalability awareness |
| 11 | Add loading states and empty states to all list pages | Low | UX polish |
| 12 | Add `max 3 scores per student-course` enforcement at the API level | Medium | Business rule enforcement |

---

## Score Card

| Dimension | Score (1-10) | Notes |
|-----------|:---:|-------|
| **Architecture** | 8 | Clean monorepo, schema-first DB, proper layering (minus service layer) |
| **Code Quality** | 8 | Readable, consistent patterns, Java records, modern Angular |
| **Testing** | 3 | Only DB migration tests — biggest gap |
| **API Design** | 9 | RESTful, paginated, searchable, documented with OpenAPI |
| **Frontend** | 8 | Modern Angular 21, Material UI, reactive patterns, responsive |
| **Database** | 9 | Well-indexed, constrained, cascade deletes, schema-first |
| **DevOps/Tooling** | 7 | ESLint + Prettier, gitflow, but no CI/CD pipeline |

**Overall: 7.4 / 10**

---

## Final Notes

This candidate clearly understands full-stack development and makes pragmatic architectural decisions. The code is clean and follows modern conventions for both Angular and Spring Boot. The main area for growth is testing — adding test coverage would elevate this submission significantly. The lack of a service layer is a minor concern that's forgivable in a POC but would be expected in a production codebase.

The candidate demonstrates they can ship a working product end-to-end, which is often more valuable than theoretical perfection.
