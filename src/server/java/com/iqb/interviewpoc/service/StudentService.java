package com.iqb.interviewpoc.service;

import com.iqb.interviewpoc.dto.StudentListDto;
import com.iqb.interviewpoc.dto.StudentListDto.CourseAverageDto;
import com.iqb.interviewpoc.entity.Student;
import com.iqb.interviewpoc.exception.ResourceNotFoundException;
import com.iqb.interviewpoc.repository.CompletedCourseProjection;
import com.iqb.interviewpoc.repository.ExamResultRepository;
import com.iqb.interviewpoc.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository repository;
    private final ExamResultRepository examResultRepository;

    public StudentService(StudentRepository repository, ExamResultRepository examResultRepository) {
        this.repository = repository;
        this.examResultRepository = examResultRepository;
    }

    public Page<StudentListDto> getAll(String search, Pageable pageable) {
        Page<Student> students;
        if (search != null && !search.isBlank()) {
            students = repository.search(search.trim(), pageable);
        } else {
            students = repository.findAll(pageable);
        }

        List<Long> studentIds = students.getContent().stream().map(Student::getId).toList();

        Map<Long, List<CourseAverageDto>> coursesByStudent;
        if (studentIds.isEmpty()) {
            coursesByStudent = Collections.emptyMap();
        } else {
            List<CompletedCourseProjection> projections = examResultRepository.findCompletedCourseAverages(studentIds);
            coursesByStudent = projections.stream()
                    .collect(Collectors.groupingBy(
                            CompletedCourseProjection::getStudentId,
                            Collectors.mapping(
                                    p -> new CourseAverageDto(p.getCourseName(), Math.round(p.getAverage() * 100.0) / 100.0),
                                    Collectors.toList()
                            )
                    ));
        }

        return students.map(s -> new StudentListDto(
                s.getId(),
                s.getFullName(),
                s.getNumber(),
                s.getEmail(),
                s.getGsmNumber(),
                coursesByStudent.getOrDefault(s.getId(), Collections.emptyList())
        ));
    }

    public Student getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }

    public Student create(Student student) {
        student.setId(null);
        return repository.save(student);
    }

    public Student update(Long id, Student student) {
        Student existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        existing.setFullName(student.getFullName());
        existing.setNumber(student.getNumber());
        existing.setEmail(student.getEmail());
        existing.setGsmNumber(student.getGsmNumber());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found");
        }
        repository.deleteById(id);
    }
}
