package com.iqb.interviewpoc.dto;

import java.util.List;

public record DashboardStatsDto(
    long totalStudents,
    long totalCourses,
    long totalExamResults,
    double averageScore,
    long completedPairs,
    long inProgressPairs,
    List<TopStudentDto> topStudents,
    List<RecentResultDto> recentResults,
    ScoreDistributionDto scoreDistribution
) {
    public record TopStudentDto(
        long studentId,
        String fullName,
        double averageScore
    ) {}

    public record RecentResultDto(
        long id,
        String studentName,
        String courseName,
        int score,
        String createdAt
    ) {}

    public record ScoreDistributionDto(
        int range0to20,
        int range21to40,
        int range41to60,
        int range61to80,
        int range81to100
    ) {}
}
