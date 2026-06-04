package com.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record SignupRequest(
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(min = 8, max = 100) String password
    ) {
    }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank String password
    ) {
    }

    public record TeacherResponse(
            Long id,
            String fullName,
            String email
    ) {
    }

    public record AuthResponse(
            String token,
            TeacherResponse teacher
    ) {
    }
}
