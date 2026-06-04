package com.api.service;

import com.api.dto.AssignmentDtos;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentService {

    List<AssignmentDtos.AssignmentResponse> list(Long departmentId, Long courseId, LocalDate dueDate, LocalDate startDate, LocalDate endDate);

    AssignmentDtos.AssignmentResponse create(AssignmentDtos.AssignmentRequest request);

    AssignmentDtos.AssignmentResponse update(Long id, AssignmentDtos.AssignmentRequest request);

    void delete(Long id);
}
