package com.iqb.interviewpoc.controller;

import com.iqb.interviewpoc.entity.Course;
import com.iqb.interviewpoc.repository.CourseRepository;
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

    private final CourseRepository repository;

    public CourseController(CourseRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "List courses", description = "Returns a paginated list of courses with optional search")
    public Page<Course> getAll(
            @Parameter(description = "Search by course name") @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return repository.search(search.trim(), pageable);
        }
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    @ApiResponse(responseCode = "200", description = "Course found")
    @ApiResponse(responseCode = "404", description = "Course not found")
    public ResponseEntity<Course> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a course")
    @ApiResponse(responseCode = "201", description = "Course created")
    public ResponseEntity<Course> create(@Valid @RequestBody Course course) {
        course.setId(null);
        Course saved = repository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a course")
    @ApiResponse(responseCode = "200", description = "Course updated")
    @ApiResponse(responseCode = "404", description = "Course not found")
    public ResponseEntity<Course> update(@PathVariable Long id, @Valid @RequestBody Course course) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(course.getName());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    @ApiResponse(responseCode = "204", description = "Course deleted")
    @ApiResponse(responseCode = "404", description = "Course not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
