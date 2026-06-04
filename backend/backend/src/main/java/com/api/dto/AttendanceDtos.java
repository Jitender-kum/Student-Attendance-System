package com.api.dto;

import com.api.enums.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public final class AttendanceDtos {

    private AttendanceDtos() {
    }

    public record AttendanceUpsertRequest(
            @NotNull Long studentId,
            @NotNull LocalDate date,
            @NotNull AttendanceStatus status
    ) {
    }

    public record BulkAttendanceRequest(
            @NotEmpty List<@Valid AttendanceUpsertRequest> records
    ) {
    }

    public record ClearAttendanceRequest(
            @NotEmpty List<@NotNull Long> studentIds,
            @NotNull LocalDate date
    ) {
    }

    public record AttendanceResponse(
            Long id,
            Long studentId,
            String studentName,
            String rollNumber,
            LocalDate date,
            AttendanceStatus status
    ) {
    }

    public record AttendanceStudentRow(
            Long studentId,
            String studentName,
            String rollNumber,
            Long departmentId,
            String departmentName,
            Long courseId,
            String courseName,
            Integer year,
            Integer semester,
            AttendanceResponse attendance
    ) {
    }

    public record ClearAttendanceResponse(
            long clearedCount
    ) {
    }
}
