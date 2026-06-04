package com.api.dto;

public final class ReportDtos {

    private ReportDtos() {
    }

    public record AttendanceReportRow(
            Long studentId,
            String studentName,
            String rollNumber,
            String departmentName,
            String courseName,
            String subjectName,
            String periodLabel,
            String startTime,
            String endTime,
            String sessionDate,
            String sessionName,
            Integer year,
            Integer semester,
            long presentCount,
            long absentCount,
            long lateCount,
            long leaveCount,
            long halfDayCount,
            long totalWorkingDays,
            double attendancePercentage
    ) {
    }
}
