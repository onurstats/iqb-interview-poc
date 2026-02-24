package com.iqb.interviewpoc.dto;

import java.util.List;

public record StudentScoresDto(
    List<CourseScoresDto> courses
) {
    public record CourseScoresDto(
        long courseId,
        String courseName,
        List<ScoreEntryDto> scores
    ) {}

    public record ScoreEntryDto(
        Long id,
        Integer score
    ) {}
}
