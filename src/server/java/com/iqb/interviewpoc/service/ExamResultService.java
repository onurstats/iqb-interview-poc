package com.iqb.interviewpoc.service;

import com.iqb.interviewpoc.dto.ExamResultDto;
import com.iqb.interviewpoc.dto.SaveScoresRequest;
import com.iqb.interviewpoc.dto.StudentScoresDto;
import com.iqb.interviewpoc.dto.StudentScoresDto.CourseScoresDto;
import com.iqb.interviewpoc.dto.StudentScoresDto.ScoreEntryDto;
import com.iqb.interviewpoc.entity.Course;
import com.iqb.interviewpoc.entity.ExamResult;
import com.iqb.interviewpoc.entity.Student;
import com.iqb.interviewpoc.exception.BusinessRuleException;
import com.iqb.interviewpoc.exception.ResourceNotFoundException;
import com.iqb.interviewpoc.repository.CourseRepository;
import com.iqb.interviewpoc.repository.ExamResultRepository;
import com.iqb.interviewpoc.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public ExamResultService(ExamResultRepository examResultRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.examResultRepository = examResultRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public Page<ExamResultDto> getAll(String search, Pageable pageable) {
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

    public StudentScoresDto getStudentScores(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found");
        }

        List<ExamResult> results = examResultRepository.findByStudentIdOrderByCourseIdAscIdAsc(studentId);

        Map<Long, List<ExamResult>> byCourse = results.stream()
                .collect(Collectors.groupingBy(er -> er.getCourse().getId(), LinkedHashMap::new, Collectors.toList()));

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

        return new StudentScoresDto(courses);
    }

    @Transactional
    public StudentScoresDto saveStudentScores(Long studentId, SaveScoresRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        for (SaveScoresRequest.CourseScoreEntry entry : request.courses()) {
            Optional<Course> courseOpt = courseRepository.findById(entry.courseId());
            if (courseOpt.isEmpty()) continue;
            Course course = courseOpt.get();

            for (SaveScoresRequest.ScoreValue sv : entry.scores()) {
                if (sv.score() == null) {
                    if (sv.id() != null) {
                        examResultRepository.deleteById(sv.id());
                    }
                    continue;
                }
                if (sv.id() != null) {
                    examResultRepository.findById(sv.id()).ifPresent(er -> {
                        er.setScore(sv.score());
                        examResultRepository.save(er);
                    });
                } else {
                    long count = examResultRepository.countByStudentIdAndCourseId(studentId, entry.courseId());
                    if (count >= 3) {
                        throw new BusinessRuleException("Maximum 3 scores per student-course pair");
                    }
                    ExamResult er = new ExamResult();
                    er.setStudent(student);
                    er.setCourse(course);
                    er.setScore(sv.score());
                    examResultRepository.save(er);
                }
            }
        }

        return getStudentScores(studentId);
    }
}
