package com.api.service;

import com.api.dto.AttendanceDtos;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    List<AttendanceDtos.AttendanceStudentRow> getStudentsForAttendance(LocalDate date, Long departmentId, Long courseId);

    AttendanceDtos.AttendanceResponse markAttendance(AttendanceDtos.AttendanceUpsertRequest request);

    AttendanceDtos.AttendanceResponse updateAttendance(Long attendanceId, AttendanceDtos.AttendanceUpsertRequest request);

    List<AttendanceDtos.AttendanceResponse> getAttendance(Long departmentId, Long courseId, LocalDate date);

    List<AttendanceDtos.AttendanceResponse> bulkMarkAttendance(AttendanceDtos.BulkAttendanceRequest request);

    AttendanceDtos.ClearAttendanceResponse clearAttendance(AttendanceDtos.ClearAttendanceRequest request);
}
