package com.api.controller;

import com.api.dto.StudentDtos;
import com.api.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentDtos.StudentResponse>> getStudents(@RequestParam(required = false) Long departmentId,
                                                                         @RequestParam(required = false) Long courseId) {
        return ResponseEntity.ok(studentService.getStudents(departmentId, courseId));
    }

    @PostMapping
    public ResponseEntity<StudentDtos.StudentResponse> addStudent(@Valid @RequestBody StudentDtos.StudentRequest request) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDtos.StudentResponse> updateStudent(@PathVariable Long id,
                                                                     @Valid @RequestBody StudentDtos.StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
