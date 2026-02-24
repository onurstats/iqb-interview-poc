package com.iqb.interviewpoc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseMigrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void tablesExist() throws SQLException {
        try (var conn = dataSource.getConnection();
             var rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            assertTrue(tables.contains("student"), "student table should exist");
            assertTrue(tables.contains("course"), "course table should exist");
            assertTrue(tables.contains("exam_result"), "exam_result table should exist");
        }
    }

    @Test
    void studentTableHasCorrectColumns() throws SQLException {
        assertColumnsExist("student", List.of("id", "full_name", "number", "email", "gsm_number", "created_at", "updated_at"));
    }

    @Test
    void courseTableHasCorrectColumns() throws SQLException {
        assertColumnsExist("course", List.of("id", "name", "created_at", "updated_at"));
    }

    @Test
    void examResultTableHasCorrectColumns() throws SQLException {
        assertColumnsExist("exam_result", List.of("id", "student_id", "course_id", "score", "created_at", "updated_at"));
    }

    @Test
    void studentNumberIsUnique() throws SQLException {
        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM exam_result");
            stmt.execute("DELETE FROM student");
            stmt.execute("INSERT INTO student (full_name, number, email, created_at, updated_at) VALUES ('A', 1, 'a@a.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
            assertThrows(SQLException.class, () ->
                stmt.execute("INSERT INTO student (full_name, number, email, created_at, updated_at) VALUES ('B', 1, 'b@b.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
            );
        }
    }

    @Test
    void scoreCheckConstraint() throws SQLException {
        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM exam_result");
            stmt.execute("DELETE FROM student");
            stmt.execute("DELETE FROM course");
            stmt.execute("INSERT INTO student (id, full_name, number, email, created_at, updated_at) VALUES (1, 'Test', 100, 'test@test.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
            stmt.execute("INSERT INTO course (id, name, created_at, updated_at) VALUES (1, 'Math', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

            // score below 0 should fail
            assertThrows(SQLException.class, () ->
                stmt.execute("INSERT INTO exam_result (student_id, course_id, score, created_at, updated_at) VALUES (1, 1, -1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
            );

            // score above 100 should fail
            assertThrows(SQLException.class, () ->
                stmt.execute("INSERT INTO exam_result (student_id, course_id, score, created_at, updated_at) VALUES (1, 1, 101, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
            );

            // score 0 should work
            stmt.execute("INSERT INTO exam_result (student_id, course_id, score, created_at, updated_at) VALUES (1, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

            // score 100 should work
            stmt.execute("INSERT INTO exam_result (student_id, course_id, score, created_at, updated_at) VALUES (1, 1, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        }
    }

    @Test
    void foreignKeyEnforcement() throws SQLException {
        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");

            // insert with non-existent student_id should fail
            assertThrows(SQLException.class, () ->
                stmt.execute("INSERT INTO exam_result (student_id, course_id, score, created_at, updated_at) VALUES (9999, 9999, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
            );
        }
    }

    @Test
    void foreignKeyCascadeDelete() throws SQLException {
        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("DELETE FROM exam_result");
            stmt.execute("DELETE FROM student");
            stmt.execute("DELETE FROM course");

            stmt.execute("INSERT INTO student (id, full_name, number, email, created_at, updated_at) VALUES (10, 'Cascade Test', 200, 'cascade@test.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
            stmt.execute("INSERT INTO course (id, name, created_at, updated_at) VALUES (10, 'Cascade Course', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
            stmt.execute("INSERT INTO exam_result (student_id, course_id, score, created_at, updated_at) VALUES (10, 10, 75, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

            // delete student should cascade to exam_result
            stmt.execute("DELETE FROM student WHERE id = 10");

            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM exam_result WHERE student_id = 10")) {
                rs.next();
                assertEquals(0, rs.getInt(1), "exam_results should be cascade deleted when student is deleted");
            }
        }
    }

    @Test
    void indexesExist() throws SQLException {
        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            List<String> indexes = new ArrayList<>();
            try (var rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type = 'index'")) {
                while (rs.next()) {
                    indexes.add(rs.getString("name"));
                }
            }
            assertTrue(indexes.contains("idx_student_number"), "idx_student_number should exist");
            assertTrue(indexes.contains("idx_student_email"), "idx_student_email should exist");
            assertTrue(indexes.contains("idx_student_full_name"), "idx_student_full_name should exist");
            assertTrue(indexes.contains("idx_course_name"), "idx_course_name should exist");
            assertTrue(indexes.contains("idx_exam_result_student_id"), "idx_exam_result_student_id should exist");
            assertTrue(indexes.contains("idx_exam_result_course_id"), "idx_exam_result_course_id should exist");
            assertTrue(indexes.contains("idx_exam_result_student_course"), "idx_exam_result_student_course should exist");
        }
    }

    @Test
    void migrationIsIdempotent() throws SQLException {
        // Running schema.sql again should not fail (IF NOT EXISTS)
        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS student (id INTEGER PRIMARY KEY AUTOINCREMENT, full_name VARCHAR(255) NOT NULL, number INTEGER NOT NULL UNIQUE, email VARCHAR(255) NOT NULL, gsm_number VARCHAR(255), created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            stmt.execute("CREATE TABLE IF NOT EXISTS course (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(255) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            stmt.execute("CREATE TABLE IF NOT EXISTS exam_result (id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER NOT NULL, course_id INTEGER NOT NULL, score INTEGER NOT NULL CHECK (score >= 0 AND score <= 100), created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE, FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_student_number ON student(number)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_exam_result_student_course ON exam_result(student_id, course_id)");
        }
        // no exception = idempotent
    }

    private void assertColumnsExist(String table, List<String> expectedColumns) throws SQLException {
        try (var conn = dataSource.getConnection();
             var rs = conn.getMetaData().getColumns(null, null, table, null)) {
            List<String> columns = new ArrayList<>();
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
            for (String col : expectedColumns) {
                assertTrue(columns.contains(col), table + " should have column: " + col);
            }
        }
    }
}
