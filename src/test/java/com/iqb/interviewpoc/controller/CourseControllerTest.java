package com.iqb.interviewpoc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iqb.interviewpoc.entity.Course;
import com.iqb.interviewpoc.repository.CourseRepository;
import com.iqb.interviewpoc.repository.ExamResultRepository;
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
class CourseControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ExamResultRepository examResultRepository;

    @BeforeEach
    void setUp() {
        examResultRepository.deleteAll();
        courseRepository.deleteAll();
    }

    private Course createCourse(String name) {
        Course c = new Course();
        c.setName(name);
        return courseRepository.save(c);
    }

    @Test
    void listCourses() throws Exception {
        createCourse("Math");
        createCourse("Physics");

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void listCoursesWithSearch() throws Exception {
        createCourse("Mathematics");
        createCourse("Physics");

        mockMvc.perform(get("/api/courses").param("search", "Math"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Mathematics"));
    }

    @Test
    void getById() throws Exception {
        Course c = createCourse("Math");

        mockMvc.perform(get("/api/courses/{id}", c.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Math"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/courses/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCourseEndpoint() throws Exception {
        String json = """
                {"name":"Chemistry"}
                """;

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Chemistry"));
    }

    @Test
    void createCourseValidationError() throws Exception {
        String json = """
                {"name":""}
                """;

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateCourse() throws Exception {
        Course c = createCourse("Math");
        String json = """
                {"name":"Advanced Math"}
                """;

        mockMvc.perform(put("/api/courses/{id}", c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Advanced Math"));
    }

    @Test
    void updateCourseNotFound() throws Exception {
        String json = """
                {"name":"Nothing"}
                """;

        mockMvc.perform(put("/api/courses/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourse() throws Exception {
        Course c = createCourse("Math");

        mockMvc.perform(delete("/api/courses/{id}", c.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/courses/{id}", c.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourseNotFound() throws Exception {
        mockMvc.perform(delete("/api/courses/{id}", 9999))
                .andExpect(status().isNotFound());
    }
}
