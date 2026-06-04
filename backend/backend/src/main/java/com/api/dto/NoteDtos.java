package com.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class NoteDtos {

    private NoteDtos() {
    }

    public record NoteRequest(
            @NotNull Long departmentId,
            @NotNull Long courseId,
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 5000) String content,
            @NotNull LocalDate noteDate
    ) {
    }

    public record NoteResponse(
            Long id,
            Long departmentId,
            String departmentName,
            Long courseId,
            String courseName,
            String title,
            String content,
            LocalDate noteDate
    ) {
    }
}
