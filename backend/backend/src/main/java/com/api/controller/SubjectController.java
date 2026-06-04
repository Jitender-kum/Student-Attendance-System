package com.api.controller;

import com.api.dto.SubjectDtos;
import com.api.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<List<SubjectDtos.SubjectResponse>> list(@RequestParam(required = false) Long departmentId,
                                                                  @RequestParam(required = false) Long courseId,
                                                                  @RequestParam(required = false) String search) {
        return ResponseEntity.ok(subjectService.list(departmentId, courseId, search));
    }

    @PostMapping
    public ResponseEntity<SubjectDtos.SubjectResponse> create(@Valid @RequestBody SubjectDtos.SubjectRequest request) {
        return ResponseEntity.ok(subjectService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectDtos.SubjectResponse> update(@PathVariable Long id,
                                                              @Valid @RequestBody SubjectDtos.SubjectRequest request) {
        return ResponseEntity.ok(subjectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
