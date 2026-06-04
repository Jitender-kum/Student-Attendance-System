package com.api.controller;

import com.api.dto.AttendanceDtos;
import com.api.dto.AttendanceSessionDtos;
import com.api.service.AttendanceService;
import com.api.service.AttendanceSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceSessionService attendanceSessionService;

    public AttendanceController(AttendanceService attendanceService,
                                AttendanceSessionService attendanceSessionService) {
        this.attendanceService = attendanceService;
        this.attendanceSessionService = attendanceSessionService;
    }

    @GetMapping("/students")
    public ResponseEntity<List<AttendanceDtos.AttendanceStudentRow>> getStudentsForAttendance(@RequestParam LocalDate date,
                                                                                              @RequestParam(required = false) Long departmentId,
                                                                                              @RequestParam(required = false) Long courseId) {
        return ResponseEntity.ok(attendanceService.getStudentsForAttendance(date, departmentId, courseId));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceDtos.AttendanceResponse>> getAttendance(@RequestParam(required = false) LocalDate date,
                                                                                 @RequestParam(required = false) Long departmentId,
                                                                                 @RequestParam(required = false) Long courseId) {
        return ResponseEntity.ok(attendanceService.getAttendance(departmentId, courseId, date));
    }

    @PostMapping
    public ResponseEntity<AttendanceDtos.AttendanceResponse> markAttendance(@Valid @RequestBody AttendanceDtos.AttendanceUpsertRequest request) {
        return ResponseEntity.ok(attendanceService.markAttendance(request));
    }

    @PutMapping("/{attendanceId}")
    public ResponseEntity<AttendanceDtos.AttendanceResponse> updateAttendance(@PathVariable Long attendanceId,
                                                                              @Valid @RequestBody AttendanceDtos.AttendanceUpsertRequest request) {
        return ResponseEntity.ok(attendanceService.updateAttendance(attendanceId, request));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AttendanceDtos.AttendanceResponse>> bulkMarkAttendance(@Valid @RequestBody AttendanceDtos.BulkAttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.bulkMarkAttendance(request));
    }

    @PostMapping("/clear")
    public ResponseEntity<AttendanceDtos.ClearAttendanceResponse> clearAttendance(@Valid @RequestBody AttendanceDtos.ClearAttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.clearAttendance(request));
    }

    @PostMapping("/sessions")
    public ResponseEntity<AttendanceSessionDtos.AttendanceSessionResponse> createSession(@Valid @RequestBody AttendanceSessionDtos.AttendanceSessionRequest request) {
        return ResponseEntity.ok(attendanceSessionService.createSession(request));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<AttendanceSessionDtos.AttendanceSessionResponse>> listSessions(@RequestParam(required = false) LocalDate date,
                                                                                              @RequestParam(required = false) Long departmentId,
                                                                                              @RequestParam(required = false) Long courseId,
                                                                                              @RequestParam(required = false) Long subjectId,
                                                                                              @RequestParam(required = false) String sessionName) {
        return ResponseEntity.ok(attendanceSessionService.listSessionsByDate(date, departmentId, courseId, subjectId, sessionName));
    }

    @GetMapping("/sessions/range")
    public ResponseEntity<List<AttendanceSessionDtos.AttendanceSessionResponse>> listSessionsByDateRange(@RequestParam LocalDate startDate,
                                                                                                          @RequestParam LocalDate endDate,
                                                                                                          @RequestParam(required = false) Long departmentId,
                                                                                                          @RequestParam(required = false) Long courseId,
                                                                                                          @RequestParam(required = false) Long subjectId,
                                                                                                          @RequestParam(required = false) String sessionName) {
        return ResponseEntity.ok(attendanceSessionService.listSessionsByDateRange(startDate, endDate, departmentId, courseId, subjectId, sessionName));
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<AttendanceSessionDtos.AttendanceSessionDetailResponse> getSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(attendanceSessionService.getSession(sessionId));
    }

    @PutMapping("/sessions/{sessionId}")
    public ResponseEntity<AttendanceSessionDtos.AttendanceSessionResponse> updateSession(@PathVariable Long sessionId,
                                                                                         @Valid @RequestBody AttendanceSessionDtos.AttendanceSessionRequest request) {
        return ResponseEntity.ok(attendanceSessionService.updateSession(sessionId, request));
    }

    @PutMapping("/sessions/{sessionId}/entries/{entryId}")
    public ResponseEntity<AttendanceSessionDtos.AttendanceEntryResponse> updateEntry(@PathVariable Long sessionId,
                                                                                     @PathVariable Long entryId,
                                                                                     @Valid @RequestBody AttendanceSessionDtos.AttendanceEntryUpdateRequest request) {
        return ResponseEntity.ok(attendanceSessionService.updateEntry(sessionId, entryId, request));
    }

    @PostMapping("/sessions/{sessionId}/entries/bulk")
    public ResponseEntity<AttendanceSessionDtos.AttendanceSessionDetailResponse> bulkUpsertEntries(@PathVariable Long sessionId,
                                                                                                   @Valid @RequestBody AttendanceSessionDtos.AttendanceSessionEntryBulkRequest request) {
        return ResponseEntity.ok(attendanceSessionService.bulkUpsertEntries(sessionId, request));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        attendanceSessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
