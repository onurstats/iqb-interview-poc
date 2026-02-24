package com.iqb.interviewpoc.controller;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ExamResultRepository examResultRepository;

    @BeforeEach
    void setUp() {
        examResultRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void statsEmpty() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").value(0))
                .andExpect(jsonPath("$.totalCourses").value(0))
                .andExpect(jsonPath("$.totalExamResults").value(0))
                .andExpect(jsonPath("$.averageScore").value(0.0))
                .andExpect(jsonPath("$.topStudents").isArray())
                .andExpect(jsonPath("$.recentResults").isArray())
                .andExpect(jsonPath("$.scoreDistribution").isMap());
    }

    @Test
    void statsWithData() throws Exception {
        Student s = new Student();
        s.setFullName("Alice");
        s.setNumber(1);
        s.setEmail("alice@test.com");
        s = studentRepository.save(s);

        Course c = new Course();
        c.setName("Math");
        c = courseRepository.save(c);

        ExamResult er = new ExamResult();
        er.setStudent(s);
        er.setCourse(c);
        er.setScore(80);
        examResultRepository.save(er);

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").value(1))
                .andExpect(jsonPath("$.totalCourses").value(1))
                .andExpect(jsonPath("$.totalExamResults").value(1))
                .andExpect(jsonPath("$.averageScore").value(80.0))
                .andExpect(jsonPath("$.completedPairs").value(0))
                .andExpect(jsonPath("$.inProgressPairs").value(1));
    }
}
