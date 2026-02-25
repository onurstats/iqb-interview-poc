package com.iqb.interviewpoc.dto;

import java.util.List;

public record StudentListDto(
    Long id,
    String fullName,
    Integer number,
    String email,
    String gsmNumber,
    List<CourseAverageDto> completedCourses
) {
    public record CourseAverageDto(String courseName, double average) {}
}
