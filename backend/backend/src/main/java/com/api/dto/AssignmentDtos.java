package com.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class AssignmentDtos {

    private AssignmentDtos() {
    }

    public record AssignmentRequest(
            @NotNull Long departmentId,
            @NotNull Long courseId,
            @NotBlank @Size(max = 180) String title,
            @Size(max = 2000) String description,
            @NotNull LocalDate dueDate
    ) {
    }

    public record AssignmentResponse(
            Long id,
            Long departmentId,
            String departmentName,
            Long courseId,
            String courseName,
            String title,
            String description,
            LocalDate dueDate
    ) {
    }
}
