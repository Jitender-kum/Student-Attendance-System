package com.api.controller;

import com.api.dto.AssignmentDtos;
import com.api.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public ResponseEntity<List<AssignmentDtos.AssignmentResponse>> list(@RequestParam(required = false) Long departmentId,
                                                                        @RequestParam(required = false) Long courseId,
                                                                        @RequestParam(required = false) LocalDate dueDate,
                                                                        @RequestParam(required = false) LocalDate startDate,
                                                                        @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok(assignmentService.list(departmentId, courseId, dueDate, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<AssignmentDtos.AssignmentResponse> create(@Valid @RequestBody AssignmentDtos.AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentDtos.AssignmentResponse> update(@PathVariable Long id,
                                                                    @Valid @RequestBody AssignmentDtos.AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
