package com.api.controller;

import com.api.dto.CourseDtos;
import com.api.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseDtos.CourseResponse>> list(@RequestParam(required = false) Long departmentId) {
        return ResponseEntity.ok(courseService.list(departmentId));
    }

    @PostMapping
    public ResponseEntity<CourseDtos.CourseResponse> create(@Valid @RequestBody CourseDtos.CourseRequest request) {
        return ResponseEntity.ok(courseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDtos.CourseResponse> update(@PathVariable Long id,
                                                            @Valid @RequestBody CourseDtos.CourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
