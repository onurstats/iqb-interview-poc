package com.iqb.interviewpoc.repository;

import com.iqb.interviewpoc.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
