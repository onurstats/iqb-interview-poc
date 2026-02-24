package com.iqb.interviewpoc.controller;

import com.iqb.interviewpoc.dto.ExamResultDto;
import com.iqb.interviewpoc.dto.SaveScoresRequest;
import com.iqb.interviewpoc.dto.StudentScoresDto;
import com.iqb.interviewpoc.dto.StudentScoresDto.CourseScoresDto;
import com.iqb.interviewpoc.dto.StudentScoresDto.ScoreEntryDto;
import com.iqb.interviewpoc.entity.Course;
import com.iqb.interviewpoc.entity.ExamResult;
import com.iqb.interviewpoc.entity.Student;
import com.iqb.interviewpoc.repository.CourseRepository;
import com.iqb.interviewpoc.repository.ExamResultRepository;
import com.iqb.interviewpoc.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exam-results")
public class ExamResultController {

    private final ExamResultRepository examResultRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public ExamResultController(ExamResultRepository examResultRepository,
                                StudentRepository studentRepository,
                                CourseRepository courseRepository) {
        this.examResultRepository = examResultRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public Page<ExamResultDto> getAll(@RequestParam(required = false) String search, Pageable pageable) {
        Page<ExamResult> results;
        if (search != null && !search.isBlank()) {
            results = examResultRepository.searchWithDetails(search.trim(), pageable);
        } else {
            results = examResultRepository.findAllWithDetails(pageable);
        }
        return results.map(er -> new ExamResultDto(
                er.getId(),
                er.getStudent().getId(),
                er.getStudent().getFullName(),
                er.getStudent().getNumber(),
                er.getCourse().getId(),
                er.getCourse().getName(),
                er.getScore()
        ));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentScoresDto> getStudentScores(@PathVariable Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            return ResponseEntity.notFound().build();
        }

        List<ExamResult> results = examResultRepository.findByStudentIdOrderByCourseIdAscIdAsc(studentId);

        Map<Long, List<ExamResult>> byCourse = results.stream()
                .collect(Collectors.groupingBy(er -> er.getCourse().getId(), LinkedHashMap::new, Collectors.toList()));

        // Only return courses that have at least one score
        List<CourseScoresDto> courses = byCourse.entrySet().stream()
                .map(entry -> {
                    List<ExamResult> courseResults = entry.getValue();
                    Course course = courseResults.get(0).getCourse();
                    List<ScoreEntryDto> scores = courseResults.stream()
                            .map(er -> new ScoreEntryDto(er.getId(), er.getScore()))
                            .collect(Collectors.toList());
                    while (scores.size() < 3) {
                        scores.add(new ScoreEntryDto(null, null));
                    }
                    return new CourseScoresDto(course.getId(), course.getName(), scores);
                })
                .toList();

        return ResponseEntity.ok(new StudentScoresDto(courses));
    }

    @PutMapping("/student/{studentId}")
    public ResponseEntity<StudentScoresDto> saveStudentScores(
            @PathVariable Long studentId,
            @RequestBody SaveScoresRequest request) {

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Student student = studentOpt.get();

        for (SaveScoresRequest.CourseScoreEntry entry : request.courses()) {
            Optional<Course> courseOpt = courseRepository.findById(entry.courseId());
            if (courseOpt.isEmpty()) continue;
            Course course = courseOpt.get();

            for (SaveScoresRequest.ScoreValue sv : entry.scores()) {
                if (sv.score() == null) {
                    // Delete if id exists but score is null
                    if (sv.id() != null) {
                        examResultRepository.deleteById(sv.id());
                    }
                    continue;
                }
                if (sv.id() != null) {
                    // Update existing
                    examResultRepository.findById(sv.id()).ifPresent(er -> {
                        er.setScore(sv.score());
                        examResultRepository.save(er);
                    });
                } else {
                    // Create new
                    ExamResult er = new ExamResult();
                    er.setStudent(student);
                    er.setCourse(course);
                    er.setScore(sv.score());
                    examResultRepository.save(er);
                }
            }
        }

        // Return updated scores
        return getStudentScores(studentId);
    }
}
