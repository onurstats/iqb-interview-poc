package com.iqb.interviewpoc.controller;

import com.iqb.interviewpoc.dto.ExamResultDto;
import com.iqb.interviewpoc.dto.SaveScoresRequest;
import com.iqb.interviewpoc.dto.StudentScoresDto;
import com.iqb.interviewpoc.service.ExamResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam-results")
@Tag(name = "Exam Results", description = "Exam score management")
public class ExamResultController {

    private final ExamResultService service;

    public ExamResultController(ExamResultService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List exam results", description = "Returns a paginated list of all exam results with optional search")
    public Page<ExamResultDto> getAll(
            @Parameter(description = "Search by student name, course name, or score") @RequestParam(required = false) String search,
            Pageable pageable) {
        return service.getAll(search, pageable);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get scores for a student", description = "Returns all exam scores grouped by course for a given student")
    @ApiResponse(responseCode = "200", description = "Scores found")
    @ApiResponse(responseCode = "404", description = "Student not found")
    public StudentScoresDto getStudentScores(@PathVariable Long studentId) {
        return service.getStudentScores(studentId);
    }

    @PutMapping("/student/{studentId}")
    @Operation(summary = "Save scores for a student", description = "Creates, updates, or deletes exam scores for a student across multiple courses")
    @ApiResponse(responseCode = "200", description = "Scores saved")
    @ApiResponse(responseCode = "400", description = "Score limit exceeded")
    @ApiResponse(responseCode = "404", description = "Student not found")
    public StudentScoresDto saveStudentScores(
            @PathVariable Long studentId,
            @Valid @RequestBody SaveScoresRequest request) {
        return service.saveStudentScores(studentId, request);
    }
}
