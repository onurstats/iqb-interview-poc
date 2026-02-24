package com.iqb.interviewpoc.controller;

import com.iqb.interviewpoc.dto.DashboardStatsDto;
import com.iqb.interviewpoc.dto.DashboardStatsDto.*;
import com.iqb.interviewpoc.repository.CourseRepository;
import com.iqb.interviewpoc.repository.ExamResultRepository;
import com.iqb.interviewpoc.repository.StudentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics")
public class DashboardController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ExamResultRepository examResultRepository;

    public DashboardController(StudentRepository studentRepository,
                               CourseRepository courseRepository,
                               ExamResultRepository examResultRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.examResultRepository = examResultRepository;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Returns summary stats including totals, averages, top students, recent results, and score distribution")
    public DashboardStatsDto getStats() {
        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        long totalExamResults = examResultRepository.count();

        Double avgScore = examResultRepository.averageScore();
        double averageScore = avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0;

        long completedPairs = examResultRepository.countCompletedPairs();
        long inProgressPairs = examResultRepository.countInProgressPairs();

        List<TopStudentDto> topStudents = examResultRepository.findTopStudentsByAvgScore()
            .stream()
            .map(row -> new TopStudentDto(
                ((Number) row[0]).longValue(),
                (String) row[1],
                Math.round(((Number) row[2]).doubleValue() * 100.0) / 100.0
            ))
            .toList();

        List<RecentResultDto> recentResults = examResultRepository.findRecentResults()
            .stream()
            .map(row -> new RecentResultDto(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                ((Number) row[3]).intValue(),
                row[4].toString()
            ))
            .toList();

        List<Integer> allScores = examResultRepository.findAllScores();
        int r0 = 0, r1 = 0, r2 = 0, r3 = 0, r4 = 0;
        for (int score : allScores) {
            if (score <= 20) r0++;
            else if (score <= 40) r1++;
            else if (score <= 60) r2++;
            else if (score <= 80) r3++;
            else r4++;
        }

        return new DashboardStatsDto(
            totalStudents, totalCourses, totalExamResults,
            averageScore, completedPairs, inProgressPairs,
            topStudents, recentResults,
            new ScoreDistributionDto(r0, r1, r2, r3, r4)
        );
    }
}
