package com.iqb.interviewpoc.service;

import com.iqb.interviewpoc.entity.Course;
import com.iqb.interviewpoc.exception.ResourceNotFoundException;
import com.iqb.interviewpoc.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository repository;

    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }

    public Page<Course> getAll(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return repository.search(search.trim(), pageable);
        }
        return repository.findAll(pageable);
    }

    public Course getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    public Course create(Course course) {
        course.setId(null);
        return repository.save(course);
    }

    public Course update(Long id, Course course) {
        Course existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        existing.setName(course.getName());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found");
        }
        repository.deleteById(id);
    }
}
