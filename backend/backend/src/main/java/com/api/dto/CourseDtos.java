package com.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class CourseDtos {

    private CourseDtos() {
    }

    public record CourseRequest(
            @NotBlank @Size(max = 120) String name,
            @Size(max = 60) String code,
            @NotNull Long departmentId
    ) {
    }

    public record CourseResponse(
            Long id,
            String name,
            String code,
            Long departmentId,
            String departmentName
    ) {
    }
}
