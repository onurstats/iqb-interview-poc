package com.iqb.interviewpoc.controller;

import com.iqb.interviewpoc.entity.Student;
import com.iqb.interviewpoc.repository.StudentRepository;
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

    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "List students", description = "Returns a paginated list of students with optional search")
    public Page<Student> getAll(
            @Parameter(description = "Search by name, number, email, or phone") @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return repository.search(search.trim(), pageable);
        }
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    @ApiResponse(responseCode = "200", description = "Student found")
    @ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a student")
    @ApiResponse(responseCode = "201", description = "Student created")
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        student.setId(null);
        Student saved = repository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a student")
    @ApiResponse(responseCode = "200", description = "Student updated")
    @ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<Student> update(@PathVariable Long id, @Valid @RequestBody Student student) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setFullName(student.getFullName());
                    existing.setNumber(student.getNumber());
                    existing.setEmail(student.getEmail());
                    existing.setGsmNumber(student.getGsmNumber());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student")
    @ApiResponse(responseCode = "204", description = "Student deleted")
    @ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
