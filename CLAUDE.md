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
├── backend/    # Spring Boot API
├── frontend/   # Angular SPA
└── docs/       # Specifications
```

## Database Schema
- **Student**: id, full_name, number, email, gsm_number
- **Course**: id, name
- **Exam_Result**: id, student_id (FK), course_id (FK), score

## Business Rules
- A course is **completed** for a student when they have exactly 3 scores for it
- Max 3 exam scores per student-course pair
- Student list must be searchable across all fields
- Selecting a student shows average scores of completed courses only

## Specs
- Full requirements in `docs/interview-POC-application-specs.pdf`
