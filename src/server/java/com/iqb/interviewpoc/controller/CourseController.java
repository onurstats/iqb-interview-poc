package com.iqb.interviewpoc.controller;

import com.iqb.interviewpoc.entity.Course;
import com.iqb.interviewpoc.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course management")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List courses", description = "Returns a paginated list of courses with optional search")
    public Page<Course> getAll(
            @Parameter(description = "Search by course name") @RequestParam(required = false) String search,
            Pageable pageable) {
        return service.getAll(search, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    @ApiResponse(responseCode = "200", description = "Course found")
    @ApiResponse(responseCode = "404", description = "Course not found")
    public Course getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create a course")
    @ApiResponse(responseCode = "201", description = "Course created")
    public ResponseEntity<Course> create(@Valid @RequestBody Course course) {
        Course saved = service.create(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a course")
    @ApiResponse(responseCode = "200", description = "Course updated")
    @ApiResponse(responseCode = "404", description = "Course not found")
    public Course update(@PathVariable Long id, @Valid @RequestBody Course course) {
        return service.update(id, course);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    @ApiResponse(responseCode = "204", description = "Course deleted")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
