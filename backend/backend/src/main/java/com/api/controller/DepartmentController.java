package com.api.controller;

import com.api.dto.DepartmentDtos;
import com.api.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDtos.DepartmentResponse>> list() {
        return ResponseEntity.ok(departmentService.list());
    }

    @PostMapping
    public ResponseEntity<DepartmentDtos.DepartmentResponse> create(@Valid @RequestBody DepartmentDtos.DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDtos.DepartmentResponse> update(@PathVariable Long id,
                                                                   @Valid @RequestBody DepartmentDtos.DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
