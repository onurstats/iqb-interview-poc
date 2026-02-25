package com.iqb.interviewpoc.service;

import com.iqb.interviewpoc.dto.DashboardStatsDto;
import com.iqb.interviewpoc.dto.DashboardStatsDto.*;
import com.iqb.interviewpoc.repository.CourseRepository;
import com.iqb.interviewpoc.repository.ExamResultRepository;
import com.iqb.interviewpoc.repository.ScoreDistributionProjection;
import com.iqb.interviewpoc.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ExamResultRepository examResultRepository;

    public DashboardService(StudentRepository studentRepository,
                            CourseRepository courseRepository,
                            ExamResultRepository examResultRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.examResultRepository = examResultRepository;
    }

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
                        row.getId(),
                        row.getFullName(),
                        Math.round(row.getAvgScore() * 100.0) / 100.0
                ))
                .toList();

        List<RecentResultDto> recentResults = examResultRepository.findRecentResults()
                .stream()
                .map(row -> new RecentResultDto(
                        row.getId(),
                        row.getFullName(),
                        row.getName(),
                        row.getScore(),
                        row.getCreatedAt()
                ))
                .toList();

        ScoreDistributionProjection dist = examResultRepository.findScoreDistribution();
        int r0 = dist.getRange0to20() != null ? dist.getRange0to20() : 0;
        int r1 = dist.getRange21to40() != null ? dist.getRange21to40() : 0;
        int r2 = dist.getRange41to60() != null ? dist.getRange41to60() : 0;
        int r3 = dist.getRange61to80() != null ? dist.getRange61to80() : 0;
        int r4 = dist.getRange81to100() != null ? dist.getRange81to100() : 0;

        return new DashboardStatsDto(
                totalStudents, totalCourses, totalExamResults,
                averageScore, completedPairs, inProgressPairs,
                topStudents, recentResults,
                new ScoreDistributionDto(r0, r1, r2, r3, r4)
        );
    }
}
