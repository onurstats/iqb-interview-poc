# Engineering Review: IQB Interview POC

**Reviewer:** AI Engineering Lead
**Date:** 2026-02-25
**Revision:** 3 (post CI/CD & UX hardening)
**Submission:** Full-stack Student/Course/Exam Management Application
**Stack:** Angular 21 + Spring Boot 3.5 + SQLite

---

## Executive Summary

This is a mature, well-structured full-stack POC that demonstrates strong engineering fundamentals across the entire stack. The application covers student management, course management, exam score tracking with a 3-score completion model, and a statistics dashboard — all in a clean monorepo with modern tooling. Since the previous review, significant improvements have been made: transaction safety on multi-entity writes, logging in exception handlers, type-safe native query projections, a CI pipeline, an unsaved-changes guard, and a centralized HTTP error interceptor.

**Recommendation: STRONG HIRE** — The candidate shows strong full-stack capability, excellent architectural instincts, thorough testing, and production-readiness awareness. The ability to systematically address review feedback across multiple iterations is a strong signal for team collaboration.

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
- RESTful endpoints with proper HTTP methods and status codes (201 Created, 204 No Content, 404 Not Found, 400 Bad Request, 409 Conflict)
- Server-side pagination with Spring Data `Pageable` on all list endpoints
- Search/filter support on all list endpoints with case-insensitive matching
- OpenAPI 3.1 documentation with Swagger UI — API is self-documenting
- Clean DTOs separate internal entities from API responses (ExamResultDto, StudentScoresDto, DashboardStatsDto)
- Structured error responses via `GlobalExceptionHandler` — no stack traces leak to clients

### 5. Frontend Quality
- Reactive search with `Subject` + `debounceTime(300ms)` + `distinctUntilChanged()` — no excessive API calls
- `MatTableDataSource` for table binding in zoneless Angular — avoids NG0100 errors
- `ChangeDetectorRef.markForCheck()` in all async callbacks — understands zoneless change detection
- Color-coded score badges (green/orange/red) — good UX attention
- Responsive CSS grid with media queries on dashboard — mobile consideration
- Course autocomplete with filtered available options — thoughtful UX
- Environment-based API URLs with `fileReplacements` in production build — deployment-ready
- **Centralized HTTP error interceptor** — snackbar notifications for 400/401/403/409/5xx; 404 passed through for component-level handling
- **Unsaved changes guard** on score editing — prevents accidental data loss with `canDeactivate` + `confirm()` dialog

### 6. Domain Modeling & Business Rules
- 3-score completion rule (student-course pair is "completed" when 3 scores exist) — correctly modeled
- **Max 3 scores per student-course pair enforced at the API level** — returns 400 with clear error message
- Score entry form handles create/update/delete in a single save operation — complex state management done well
- Dashboard aggregations (averages, distributions, top performers) — shows data thinking

### 7. Error Handling
- `@RestControllerAdvice` with `GlobalExceptionHandler` catches and translates all exception types:
  - `MethodArgumentNotValidException` → 400 with field-level details
  - `ConstraintViolationException` → 400 with property paths
  - `DataIntegrityViolationException` → 409 with user-friendly duplicate message
  - Generic `Exception` → 500 with safe message (no internal details)
- Consistent `ErrorResponse` record: `{ status, error, message, timestamp }`
- DTO-level `@Valid` / `@NotNull` on `SaveScoresRequest` — defense in depth
- **SLF4J logging at all levels**: `warn` for validation errors, `error` for data integrity and unexpected exceptions — production-debuggable

### 8. Testing
- **42 backend tests** across 5 test classes — all passing
- Schema validation tests (table structure, constraints, indexes, cascade deletes, idempotency)
- Controller integration tests with `@SpringBootTest` + `MockMvc`:
  - CRUD operations for students, courses, exam results
  - Search and pagination
  - Validation error responses (400)
  - Not-found handling (404)
  - Business rule enforcement (max 3 scores)
  - Dashboard stats with empty and populated data
- Separate test profile with isolated database (`iqb-test.db`) and no seed data

### 9. Configuration Management
- Spring profiles: `dev` (show-sql), `test` (separate DB, no seed data)
- Default profile is `dev` — safe for local development
- Frontend `environment.ts` / `environment.prod.ts` with `fileReplacements` in `angular.json`
- Production build uses same-origin empty `apiUrl` — no hardcoded URLs in prod bundle

### 10. Tooling & Developer Experience
- ESLint with `angular-eslint` (recommended + template accessibility + stylistic rules)
- Prettier integrated via `eslint-plugin-prettier` — formatting is a lint error, not a separate step
- `pnpm lint`, `pnpm format`, `pnpm format:check` scripts — CI-ready
- EditorConfig for consistent editor settings
- OpenAPI/Swagger UI at `/swagger-ui/index.html` — instant API exploration

### 11. CI/CD Pipeline
- **GitHub Actions** with two parallel jobs (backend + frontend)
- Backend: Java 21 setup → `mvn compile` → `mvn test` (42 tests)
- Frontend: pnpm + Node 22 setup → `pnpm install --frozen-lockfile` → `pnpm lint` → `tsc --noEmit` → `pnpm build`
- Triggers on push and PR to `develop` and `main` — protects both branches
- Maven and pnpm caching for faster builds

### 12. Transaction Safety
- `@Transactional` on `saveStudentScores()` — multi-entity create/update/delete operations are atomic
- Prevents partial state if a constraint violation occurs mid-loop

---

## Weaknesses (Concerns)

### 1. No Service Layer (MEDIUM)
Controllers directly inject repositories and contain all business logic. This works for a POC but is a code smell at scale:
- Business rules (3-score max, score validation, cascade operations) live in controller methods
- No place for shared business logic if multiple controllers need the same operations
- Harder to unit test business logic in isolation

**Files affected:** All controllers (`StudentController`, `CourseController`, `ExamResultController`, `DashboardController`)

**Mitigated by:** The application is small enough that the controllers are readable and each has a single responsibility. Integration tests cover the business logic effectively.

### 2. No Authentication or Authorization (LOW — out of scope)
- All endpoints are publicly accessible
- No user concept, no roles, no JWT/session handling
- CORS allows `localhost:4200` and `localhost:8080` — appropriate for dev but no production CORS strategy

**Acceptable for POC scope.**

### 3. No Frontend Tests (LOW)
- No Angular unit tests (schematics configured to skip test generation)
- No e2e tests
- Backend has strong coverage (42 tests), but frontend logic (score state management, autocomplete filtering, form validation) is untested

**Mitigated by:** Frontend components are mostly thin wrappers around service calls. The complex state management in `ExamResultDetailComponent` is the main area that would benefit from testing.

### 4. `index.html` Title (COSMETIC)
Page title is `"ClientTemp"` instead of the project name. Minor cosmetic issue.

### 5. `open-in-view` Warning (LOW)
Spring JPA `open-in-view` is enabled by default, producing a startup warning. Not harmful for this app (no lazy-load access patterns in views), but should be explicitly set to suppress the warning.

**Fix:** Add `spring.jpa.open-in-view=false` to `application.properties`.

### 6. Dashboard N+0 Query Pattern (LOW)
`DashboardController.getStats()` makes 8 separate repository calls in a single request (count, averageScore, countCompletedPairs, countInProgressPairs, findTopStudents, findRecentResults, findAllScores, count×2). For SQLite with a small dataset this is fine, but for a production database these could be consolidated.

**Mitigated by:** SQLite is in-process (no network round-trips), dataset is small, and the queries are individually simple with proper indexing.

---

## Resolved Issues (from Previous Reviews)

Issues from previous reviews that have been addressed:

| # | Issue | Resolved In | How |
|---|-------|-------------|-----|
| 1 | No global exception handling | Rev 2 | `GlobalExceptionHandler` with `ErrorResponse` record |
| 2 | Hardcoded base URLs in frontend | Rev 2 | `environment.ts` + `environment.prod.ts` + `fileReplacements` |
| 3 | Insufficient test coverage | Rev 2 | 42 tests: 10 schema + 32 controller integration tests |
| 4 | No Spring profiles | Rev 2 | `dev` and `test` profiles with separate configs |
| 5 | `show-sql` always on | Rev 2 | Moved to `application-dev.properties` only |
| 6 | No DTO validation | Rev 2 | `@NotNull` + `@Valid` on `SaveScoresRequest` fields |
| 7 | No max-3-scores enforcement | Rev 2 | `countByStudentIdAndCourseId()` + 400 error on violation |
| 8 | No `@Transactional` on multi-entity writes | **Rev 3** | `@Transactional` added to `saveStudentScores()` |
| 9 | No logging in exception handlers | **Rev 3** | SLF4J `Logger` with `warn`/`error` levels in all handlers |
| 10 | Native SQL queries return `Object[]` | **Rev 3** | Interface-based projections (`TopStudentProjection`, `RecentResultProjection`) |
| 11 | No CI pipeline | **Rev 3** | GitHub Actions with parallel backend + frontend jobs |
| 12 | No unsaved-changes guard | **Rev 3** | `canDeactivate` guard on `ExamResultDetailComponent` |
| 13 | No HTTP error interceptor | **Rev 3** | Functional `httpErrorInterceptor` with `MatSnackBar` notifications |

---

## Remaining Improvement Suggestions

| # | Improvement | Priority | Effort |
|---|------------|----------|--------|
| 1 | Extract service layer between controllers and repositories | Medium | 4-5 hrs |
| 2 | Add Angular unit tests for `ExamResultDetailComponent` | Medium | 3-4 hrs |
| 3 | Fix `index.html` title from "ClientTemp" to project name | Low | 1 min |
| 4 | Set `spring.jpa.open-in-view=false` to suppress warning | Low | 1 min |
| 5 | Add auth/authorization (JWT or session-based) | Low (POC) | 8-10 hrs |
| 6 | Add `README.md` with setup and run instructions | Low | 30 min |

---

## Score Card

| Dimension | Score (1-10) | Rev 2 | Rev 1 | Notes |
|-----------|:---:|:---:|:---:|-------|
| **Architecture** | 8 | 8 | 8 | Clean monorepo, schema-first DB, proper layering (minus service layer) |
| **Code Quality** | 8.5 | 8 | 8 | Readable, consistent patterns, Java records, modern Angular, type-safe projections |
| **Testing** | 7 | 7 | 3 | 42 backend integration tests covering all controllers, edge cases, business rules |
| **API Design** | 9 | 9 | 9 | RESTful, paginated, searchable, documented, structured error responses, transactional writes |
| **Frontend** | 8.5 | 8 | 8 | Modern Angular 21, Material UI, reactive patterns, error interceptor, unsaved-changes guard |
| **Database** | 9 | 9 | 9 | Well-indexed, constrained, cascade deletes, schema-first |
| **DevOps/Tooling** | 9 | 8 | 7 | CI pipeline, ESLint + Prettier, gitflow, Spring profiles, test isolation |

**Overall: 8.4 / 10** (up from 8.1 → 7.4)

---

## Final Notes

This submission has continued to mature since the second review. Five of the six previously-flagged weaknesses have been resolved — the only remaining structural concern is the lack of a service layer, which is a reasonable trade-off for a POC of this size.

The codebase now demonstrates:

- **Defensive coding**: Validation at DTO, entity, and database layers; structured error responses; max-3-score enforcement; transaction safety
- **Testing discipline**: 42 backend tests covering happy paths, error cases, edge cases, and business rules
- **Environment awareness**: Spring profiles for dev/test, Angular environment configs for dev/prod
- **Production hygiene**: No SQL logging by default, no stack traces in error responses, separate test database, logging in exception handlers
- **UX polish**: Unsaved-changes guard, centralized error notifications, responsive dashboard, debounced search
- **CI/CD readiness**: GitHub Actions pipeline with lint, type-check, build, and test stages

The remaining suggestions (service layer extraction, frontend tests, cosmetic fixes) are either low-priority or beyond typical POC scope. The candidate consistently demonstrates the ability to ship a working product end-to-end, respond to feedback systematically, and make pragmatic engineering decisions — all strong signals for a team environment.
