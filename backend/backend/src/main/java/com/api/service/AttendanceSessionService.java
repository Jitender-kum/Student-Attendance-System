package com.api.service;

import com.api.dto.AttendanceSessionDtos;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceSessionService {

    AttendanceSessionDtos.AttendanceSessionResponse createSession(AttendanceSessionDtos.AttendanceSessionRequest request);

    List<AttendanceSessionDtos.AttendanceSessionResponse> listSessionsByDate(LocalDate date, Long departmentId, Long courseId, Long subjectId, String sessionName);

    List<AttendanceSessionDtos.AttendanceSessionResponse> listSessionsByDateRange(LocalDate startDate, LocalDate endDate, Long departmentId, Long courseId, Long subjectId, String sessionName);

    AttendanceSessionDtos.AttendanceSessionDetailResponse getSession(Long sessionId);

    AttendanceSessionDtos.AttendanceSessionResponse updateSession(Long sessionId, AttendanceSessionDtos.AttendanceSessionRequest request);

    AttendanceSessionDtos.AttendanceEntryResponse updateEntry(Long sessionId, Long entryId, AttendanceSessionDtos.AttendanceEntryUpdateRequest request);

    AttendanceSessionDtos.AttendanceSessionDetailResponse bulkUpsertEntries(Long sessionId, AttendanceSessionDtos.AttendanceSessionEntryBulkRequest request);

    void deleteSession(Long sessionId);
}
