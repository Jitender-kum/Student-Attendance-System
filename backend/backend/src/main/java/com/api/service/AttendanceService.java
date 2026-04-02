package com.api.service;

import com.api.dto.StudentAttendanceResponseDto;
import com.api.model.StudentAttendance;

import java.util.Date;
import java.util.List;

public interface AttendanceService {
    List<StudentAttendanceResponseDto> getStudentAttendanceList(String fromDate);

    String markAttendance(StudentAttendance studentAttendance);
}
