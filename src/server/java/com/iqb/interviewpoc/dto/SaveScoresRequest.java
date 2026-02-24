package com.iqb.interviewpoc.dto;

import java.util.List;

public record SaveScoresRequest(
    List<CourseScoreEntry> courses
) {
    public record CourseScoreEntry(
        long courseId,
        List<ScoreValue> scores
    ) {}

    public record ScoreValue(
        Long id,
        Integer score
    ) {}
}
