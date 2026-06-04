package com.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class DepartmentDtos {

    private DepartmentDtos() {
    }

    public record DepartmentRequest(
            @NotBlank @Size(max = 120) String name
    ) {
    }

    public record DepartmentResponse(
            Long id,
            String name
    ) {
    }
}
