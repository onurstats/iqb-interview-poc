package com.iqb.interviewpoc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iqb.interviewpoc.entity.Course;
import com.iqb.interviewpoc.entity.ExamResult;
import com.iqb.interviewpoc.entity.Student;
import com.iqb.interviewpoc.repository.CourseRepository;
import com.iqb.interviewpoc.repository.ExamResultRepository;
import com.iqb.interviewpoc.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExamResultControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ExamResultRepository examResultRepository;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        examResultRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();

        student = new Student();
        student.setFullName("Test Student");
        student.setNumber(100);
        student.setEmail("test@test.com");
        student = studentRepository.save(student);

        course = new Course();
        course.setName("Test Course");
        course = courseRepository.save(course);
    }

    private ExamResult createScore(int score) {
        ExamResult er = new ExamResult();
        er.setStudent(student);
        er.setCourse(course);
        er.setScore(score);
        return examResultRepository.save(er);
    }

    @Test
    void listExamResults() throws Exception {
        createScore(80);
        createScore(90);

        mockMvc.perform(get("/api/exam-results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void listExamResultsWithSearch() throws Exception {
        createScore(85);

        mockMvc.perform(get("/api/exam-results").param("search", "Test Student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getStudentScores() throws Exception {
        createScore(70);
        createScore(80);

        mockMvc.perform(get("/api/exam-results/student/{id}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses", hasSize(1)))
                .andExpect(jsonPath("$.courses[0].courseName").value("Test Course"))
                .andExpect(jsonPath("$.courses[0].scores", hasSize(3)));
    }

    @Test
    void getStudentScoresNotFound() throws Exception {
        mockMvc.perform(get("/api/exam-results/student/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveScoresCreate() throws Exception {
        String json = String.format("""
                {"courses":[{"courseId":%d,"scores":[{"id":null,"score":95}]}]}
                """, course.getId());

        mockMvc.perform(put("/api/exam-results/student/{id}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses[0].scores[0].score").value(95));
    }

    @Test
    void saveScoresUpdate() throws Exception {
        ExamResult er = createScore(70);

        String json = String.format("""
                {"courses":[{"courseId":%d,"scores":[{"id":%d,"score":85}]}]}
                """, course.getId(), er.getId());

        mockMvc.perform(put("/api/exam-results/student/{id}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses[0].scores[0].score").value(85));
    }

    @Test
    void saveScoresDelete() throws Exception {
        ExamResult er = createScore(70);

        String json = String.format("""
                {"courses":[{"courseId":%d,"scores":[{"id":%d,"score":null}]}]}
                """, course.getId(), er.getId());

        mockMvc.perform(put("/api/exam-results/student/{id}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isEmpty());
    }

    @Test
    void saveScoresStudentNotFound() throws Exception {
        String json = String.format("""
                {"courses":[{"courseId":%d,"scores":[{"id":null,"score":50}]}]}
                """, course.getId());

        mockMvc.perform(put("/api/exam-results/student/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveScoresValidationError() throws Exception {
        String json = """
                {"courses":null}
                """;

        mockMvc.perform(put("/api/exam-results/student/{id}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void saveScoresEnforcesMaxThree() throws Exception {
        createScore(70);
        createScore(80);
        createScore(90);

        // Attempt to add a 4th score
        String json = String.format("""
                {"courses":[{"courseId":%d,"scores":[{"id":null,"score":95}]}]}
                """, course.getId());

        mockMvc.perform(put("/api/exam-results/student/{id}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Maximum 3 scores")));
    }
}
