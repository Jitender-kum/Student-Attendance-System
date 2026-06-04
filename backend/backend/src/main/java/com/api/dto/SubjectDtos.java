package com.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class SubjectDtos {

    private SubjectDtos() {
    }

    public record SubjectRequest(
            Long departmentId,
            @NotNull Long courseId,
            @NotBlank @Size(max = 120) String name,
            @Size(max = 60) String code,
            @Size(max = 2000) String description
    ) {
    }

    public record SubjectResponse(
            Long id,
            Long departmentId,
            String departmentName,
            Long courseId,
            String courseName,
            String name,
            String code,
            String description
    ) {
    }
}
