# IQB Interview POC Application

## Project Overview
A POC web application for managing students, courses, and exam results.

## Tech Stack
- **Backend**: Java Spring Boot (Maven)
- **Frontend**: Angular
- **Database**: SQLite
- **ORM**: Spring Data JPA / Hibernate

## Project Structure
```
iqb-interview-poc/
├── src/
│   ├── client/          # Angular source
│   ├── server/          # Spring Boot source
│   │   ├── java/com/iqb/interviewpoc/
│   │   └── resources/
│   └── test/
├── data/                # SQLite database (gitignored)
├── docs/                # Specifications
├── pom.xml              # Maven (backend)
├── package.json         # pnpm (frontend)
└── angular.json         # Angular config
```

## Database Schema
- **Student**: id, full_name, number (integer), email, gsm_number, deleted (boolean, soft delete)
- **Course**: id, name, deleted (boolean, soft delete)
- **Exam_Result**: id, student_id (FK), course_id (FK), score (0-100), deleted (boolean, soft delete)

## Business Rules
- A course is **completed** for a student when they have exactly 3 scores for it
- Max 3 exam scores per student-course pair (enforce on backend)
- Score range: 0-100
- Student list must be searchable across all fields
- Selecting a student shows **per-course** average scores of completed courses
- All entities support **soft delete** and **hard delete**

## Design Decisions
- `number` field = student registration number (integer)
- UI library: PrimeNG
- Package manager: pnpm
- Node.js: 22 LTS (via nvm)
- Java: 21 LTS (via brew openjdk@21)
- Gitflow branching strategy

## Specs
- Full requirements in `docs/interview-POC-application-specs.pdf`
