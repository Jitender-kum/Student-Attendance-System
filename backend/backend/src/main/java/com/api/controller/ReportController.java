package com.api.controller;

import com.api.dto.ReportDtos;
import com.api.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<ReportDtos.AttendanceReportRow>> weekly(@RequestParam LocalDate startDate,
                                                                       @RequestParam(required = false) Long departmentId,
                                                                       @RequestParam(required = false) Long courseId,
                                                                       @RequestParam(required = false) Long subjectId) {
        return ResponseEntity.ok(reportService.getWeeklyReport(startDate, departmentId, courseId, subjectId));
    }

    @GetMapping("/weekly/export")
    public ResponseEntity<byte[]> weeklyExport(@RequestParam LocalDate startDate,
                                               @RequestParam(required = false) Long departmentId,
                                               @RequestParam(required = false) Long courseId,
                                               @RequestParam(required = false) Long subjectId,
                                               @RequestParam(required = false) List<Long> studentIds) {
        return excelResponse(
                reportService.exportWeeklyReport(startDate, departmentId, courseId, subjectId, studentIds),
                "attendance-weekly-" + startDate + ".xlsx"
        );
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<ReportDtos.AttendanceReportRow>> monthly(@RequestParam int year,
                                                                        @RequestParam int month,
                                                                        @RequestParam(required = false) Long departmentId,
                                                                        @RequestParam(required = false) Long courseId,
                                                                        @RequestParam(required = false) Long subjectId) {
        return ResponseEntity.ok(reportService.getMonthlyReport(year, month, departmentId, courseId, subjectId));
    }

    @GetMapping("/monthly/export")
    public ResponseEntity<byte[]> monthlyExport(@RequestParam int year,
                                                @RequestParam int month,
                                                @RequestParam(required = false) Long departmentId,
                                                @RequestParam(required = false) Long courseId,
                                                @RequestParam(required = false) Long subjectId,
                                                @RequestParam(required = false) List<Long> studentIds) {
        return excelResponse(
                reportService.exportMonthlyReport(year, month, departmentId, courseId, subjectId, studentIds),
                "attendance-monthly-" + year + "-" + String.format("%02d", month) + ".xlsx"
        );
    }

    @GetMapping("/semester")
    public ResponseEntity<List<ReportDtos.AttendanceReportRow>> semester(@RequestParam(required = false) Integer year,
                                                                         @RequestParam Integer semester,
                                                                         @RequestParam(required = false) Long departmentId,
                                                                         @RequestParam(required = false) Long courseId,
                                                                         @RequestParam(required = false) Long subjectId) {
        return ResponseEntity.ok(reportService.getSemesterReport(year, semester, departmentId, courseId, subjectId));
    }

    @GetMapping("/semester/export")
    public ResponseEntity<byte[]> semesterExport(@RequestParam(required = false) Integer year,
                                                 @RequestParam Integer semester,
                                                 @RequestParam(required = false) Long departmentId,
                                                 @RequestParam(required = false) Long courseId,
                                                 @RequestParam(required = false) Long subjectId,
                                                 @RequestParam(required = false) List<Long> studentIds) {
        String yearPart = year != null ? year.toString() : "all";
        return excelResponse(
                reportService.exportSemesterReport(year, semester, departmentId, courseId, subjectId, studentIds),
                "attendance-semester-year-" + yearPart + "-sem-" + semester + ".xlsx"
        );
    }

    private ResponseEntity<byte[]> excelResponse(byte[] bytes, String fileName) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
