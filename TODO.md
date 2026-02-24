# IQB Interview POC - TODO

## Setup
- [x] Create project repository
- [x] Create feature/docs branch (gitflow)
- [x] Add specs PDF to docs/
- [x] Create CLAUDE.md
- [x] Install Java 21 LTS, Maven, Node 22 LTS, pnpm, Angular CLI

## Backend (Spring Boot + SQLite)
- [ ] Initialize Spring Boot project with Maven
- [ ] Configure SQLite datasource + JPA/Hibernate
- [ ] Create Student entity, repository, service, controller
- [ ] Create Course entity, repository, service, controller
- [ ] Create ExamResult entity, repository, service, controller
- [ ] Implement business rule: max 3 scores per student-course
- [ ] Implement score validation (0-100 range)
- [ ] Implement completed course logic (3 scores = completed)
- [ ] Implement per-course average score calculation for completed courses
- [ ] Implement soft delete + hard delete for all entities
- [ ] Add search endpoint for students (all fields)
- [ ] Configure CORS for Angular dev server

## Frontend (Angular + PrimeNG)
- [ ] Initialize Angular project with pnpm
- [ ] Install and configure PrimeNG
- [ ] Create shared models (Student, Course, ExamResult)
- [ ] Create API services (StudentService, CourseService, ExamResultService)
- [ ] Create Student list page with search across all fields
- [ ] Create Student form (add/edit)
- [ ] Create Course list page
- [ ] Create Course form (add/edit)
- [ ] Create Exam score entry (select student + course, enter score)
- [ ] Create Student detail view with completed course averages
- [ ] Set up routing and navigation

## Integration & Polish
- [ ] Proxy config for Angular → Spring Boot
- [ ] End-to-end testing
- [ ] Update README with setup/run instructions

## Git (Gitflow)
- [ ] Finish feature/docs
- [ ] Create feature/backend
- [ ] Create feature/frontend
- [ ] Create feature/integration
- [ ] Merge to develop → main
