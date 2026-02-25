-- =============================================================================
-- IQB Interview POC - Database Schema (SQLite)
-- =============================================================================
--
-- Tables:
--   student      - Student personal information
--   course       - Course names
--   exam_result  - Exam scores per student/course (max 3 per pair = completed)
--
-- Relationships:
--   exam_result.student_id -> student.id (CASCADE delete)
--   exam_result.course_id  -> course.id  (CASCADE delete)
--
-- Constraints:
--   student.number  - unique registration number
--   exam_result.score - CHECK 0-100
--
-- Indexes:
--   student:     number, email, full_name (search across all fields)
--   course:      name
--   exam_result: student_id, course_id, composite(student_id, course_id)
--
-- Notes:
--   - Foreign keys enforced via PRAGMA foreign_keys = ON (per-connection)
--   - Timestamps managed by JPA @PrePersist / @PreUpdate
--   - All CREATE statements are idempotent (IF NOT EXISTS)
-- =============================================================================

CREATE TABLE IF NOT EXISTS student (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name VARCHAR(255) NOT NULL,
    number INTEGER NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    gsm_number VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS course (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exam_result (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL,
    score INTEGER NOT NULL CHECK (score >= 0 AND score <= 100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_student_number ON student(number);
CREATE INDEX IF NOT EXISTS idx_student_email ON student(email);
CREATE INDEX IF NOT EXISTS idx_student_full_name ON student(full_name);
CREATE INDEX IF NOT EXISTS idx_course_name ON course(name);
CREATE INDEX IF NOT EXISTS idx_exam_result_student_id ON exam_result(student_id);
CREATE INDEX IF NOT EXISTS idx_exam_result_course_id ON exam_result(course_id);
CREATE INDEX IF NOT EXISTS idx_exam_result_student_course ON exam_result(student_id, course_id);
