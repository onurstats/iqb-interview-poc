package com.iqb.interviewpoc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iqb.interviewpoc.entity.Student;
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
class StudentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ExamResultRepository examResultRepository;

    @BeforeEach
    void setUp() {
        examResultRepository.deleteAll();
        studentRepository.deleteAll();
    }

    private Student createStudent(String name, int number, String email) {
        Student s = new Student();
        s.setFullName(name);
        s.setNumber(number);
        s.setEmail(email);
        return studentRepository.save(s);
    }

    @Test
    void listStudents() throws Exception {
        createStudent("Alice", 1, "alice@test.com");
        createStudent("Bob", 2, "bob@test.com");

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void listStudentsWithSearch() throws Exception {
        createStudent("Alice Smith", 1, "alice@test.com");
        createStudent("Bob Jones", 2, "bob@test.com");

        mockMvc.perform(get("/api/students").param("search", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("Alice Smith"));
    }

    @Test
    void getById() throws Exception {
        Student s = createStudent("Alice", 1, "alice@test.com");

        mockMvc.perform(get("/api/students/{id}", s.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Alice"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/students/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStudent() throws Exception {
        String json = """
                {"fullName":"Charlie","number":10,"email":"charlie@test.com"}
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.fullName").value("Charlie"));
    }

    @Test
    void createStudentValidationError() throws Exception {
        String json = """
                {"number":10,"email":"bad"}
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateStudent() throws Exception {
        Student s = createStudent("Alice", 1, "alice@test.com");
        String json = """
                {"fullName":"Alice Updated","number":1,"email":"alice@test.com"}
                """;

        mockMvc.perform(put("/api/students/{id}", s.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Alice Updated"));
    }

    @Test
    void updateStudentNotFound() throws Exception {
        String json = """
                {"fullName":"Nobody","number":1,"email":"no@test.com"}
                """;

        mockMvc.perform(put("/api/students/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent() throws Exception {
        Student s = createStudent("Alice", 1, "alice@test.com");

        mockMvc.perform(delete("/api/students/{id}", s.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/students/{id}", s.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudentNotFound() throws Exception {
        mockMvc.perform(delete("/api/students/{id}", 9999))
                .andExpect(status().isNotFound());
    }
}
