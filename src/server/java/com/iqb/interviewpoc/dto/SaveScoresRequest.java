package com.iqb.interviewpoc.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaveScoresRequest(
    @NotNull @Valid List<CourseScoreEntry> courses
) {
    public record CourseScoreEntry(
        long courseId,
        @NotNull List<ScoreValue> scores
    ) {}

    public record ScoreValue(
        Long id,
        Integer score
    ) {}
}
