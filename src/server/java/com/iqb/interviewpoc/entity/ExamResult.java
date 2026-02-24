package com.iqb.interviewpoc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "exam_result", indexes = {
    @Index(name = "idx_exam_result_student_id", columnList = "student_id"),
    @Index(name = "idx_exam_result_course_id", columnList = "course_id"),
    @Index(name = "idx_exam_result_student_course", columnList = "student_id, course_id")
})
public class ExamResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "score", nullable = false)
    private Integer score;

    public ExamResult() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
