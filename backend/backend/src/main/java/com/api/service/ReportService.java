package com.api.service;

import com.api.dto.ReportDtos;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<ReportDtos.AttendanceReportRow> getWeeklyReport(LocalDate startDate, Long departmentId, Long courseId, Long subjectId);

    List<ReportDtos.AttendanceReportRow> getMonthlyReport(int year, int month, Long departmentId, Long courseId, Long subjectId);

    List<ReportDtos.AttendanceReportRow> getSemesterReport(Integer year, Integer semester, Long departmentId, Long courseId, Long subjectId);

    byte[] exportWeeklyReport(LocalDate startDate, Long departmentId, Long courseId, Long subjectId, List<Long> studentIds);

    byte[] exportMonthlyReport(int year, int month, Long departmentId, Long courseId, Long subjectId, List<Long> studentIds);

    byte[] exportSemesterReport(Integer year, Integer semester, Long departmentId, Long courseId, Long subjectId, List<Long> studentIds);
}
