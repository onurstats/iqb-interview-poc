package com.iqb.interviewpoc.dto;

public record ExamResultDto(
    long id,
    long studentId,
    String studentName,
    int studentNumber,
    long courseId,
    String courseName,
    int score
) {}
