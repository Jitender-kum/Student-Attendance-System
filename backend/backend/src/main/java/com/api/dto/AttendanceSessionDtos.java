package com.api.dto;

import com.api.enums.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public final class AttendanceSessionDtos {

    private AttendanceSessionDtos() {
    }

    public record AttendanceSessionRequest(
            Long departmentId,
            Long courseId,
            Long subjectId,
            @NotNull LocalDate sessionDate,
            @NotBlank @Size(max = 120) String sessionName,
            @Size(max = 255) String subjectOrTopic,
            @Size(max = 500) String remarks,
            LocalTime startTime,
            LocalTime endTime,
            @Size(max = 50) String periodLabel
    ) {
    }

    public record AttendanceSessionResponse(
            Long id,
            Long departmentId,
            String departmentName,
            Long courseId,
            String courseName,
            Long subjectId,
            String subjectName,
            LocalDate sessionDate,
            String sessionName,
            String subjectOrTopic,
            String remarks,
            LocalTime startTime,
            LocalTime endTime,
            String periodLabel,
            Date createdAt,
            Date updatedAt
    ) {
    }

    public record AttendanceEntryUpsertRequest(
            @NotNull Long studentId,
            @NotNull AttendanceStatus status
    ) {
    }

    public record AttendanceEntryUpdateRequest(
            @NotNull AttendanceStatus status
    ) {
    }

    public record AttendanceEntryResponse(
            Long id,
            Long studentId,
            String studentName,
            String rollNumber,
            Long departmentId,
            String departmentName,
            Long courseId,
            String courseName,
            Integer year,
            Integer semester,
            AttendanceStatus status,
            Date markedAt,
            Date updatedAt
    ) {
    }

    public record AttendanceSessionDetailResponse(
            Long id,
            Long departmentId,
            String departmentName,
            Long courseId,
            String courseName,
            Long subjectId,
            String subjectName,
            LocalDate sessionDate,
            String sessionName,
            String subjectOrTopic,
            String remarks,
            LocalTime startTime,
            LocalTime endTime,
            String periodLabel,
            Date createdAt,
            Date updatedAt,
            List<AttendanceEntryResponse> entries
    ) {
    }

    public record AttendanceSessionEntryBulkRequest(
            @NotEmpty List<@Valid AttendanceEntryUpsertRequest> entries
    ) {
    }
}
