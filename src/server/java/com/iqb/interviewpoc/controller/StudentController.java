package com.iqb.interviewpoc.controller;

import com.iqb.interviewpoc.dto.StudentListDto;
import com.iqb.interviewpoc.entity.Student;
import com.iqb.interviewpoc.service.StudentService;
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
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Student management")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List students", description = "Returns a paginated list of students with optional search")
    public Page<StudentListDto> getAll(
            @Parameter(description = "Search by name, number, email, or phone") @RequestParam(required = false) String search,
            Pageable pageable) {
        return service.getAll(search, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    @ApiResponse(responseCode = "200", description = "Student found")
    @ApiResponse(responseCode = "404", description = "Student not found")
    public Student getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create a student")
    @ApiResponse(responseCode = "201", description = "Student created")
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        Student saved = service.create(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a student")
    @ApiResponse(responseCode = "200", description = "Student updated")
    @ApiResponse(responseCode = "404", description = "Student not found")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student student) {
        return service.update(id, student);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student")
    @ApiResponse(responseCode = "204", description = "Student deleted")
    @ApiResponse(responseCode = "404", description = "Student not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
