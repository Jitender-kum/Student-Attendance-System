package com.api.serviceImpl;

import com.api.dto.AttendanceDtos;
import com.api.exception.BadRequestException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.AttendanceEntry;
import com.api.model.AttendanceSession;
import com.api.model.Student;
import com.api.model.Teacher;
import com.api.repository.AttendanceEntryRepository;
import com.api.repository.AttendanceSessionRepository;
import com.api.repository.StudentRepository;
import com.api.security.AuthenticatedTeacherService;
import com.api.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final String DEFAULT_SESSION_NAME = "Default";

    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceEntryRepository attendanceEntryRepository;
    private final StudentRepository studentRepository;
    private final AuthenticatedTeacherService authenticatedTeacherService;

    public AttendanceServiceImpl(AttendanceSessionRepository attendanceSessionRepository,
                                 AttendanceEntryRepository attendanceEntryRepository,
                                 StudentRepository studentRepository,
                                 AuthenticatedTeacherService authenticatedTeacherService) {
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.attendanceEntryRepository = attendanceEntryRepository;
        this.studentRepository = studentRepository;
        this.authenticatedTeacherService = authenticatedTeacherService;
    }

    @Override
    public List<AttendanceDtos.AttendanceStudentRow> getStudentsForAttendance(LocalDate date, Long departmentId, Long courseId) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        List<Student> students = findStudents(teacher.getId(), departmentId, courseId);
        Map<Long, AttendanceEntry> attendanceMap = getDefaultAttendanceMap(teacher.getId(), date, departmentId, courseId);

        return students.stream()
                .map(student -> {
                    AttendanceEntry attendance = attendanceMap.get(student.getId());
                    return new AttendanceDtos.AttendanceStudentRow(
                            student.getId(),
                            student.getName(),
                            student.getRollNo(),
                            student.getDepartmentRef() != null ? student.getDepartmentRef().getId() : null,
                            student.getDepartmentRef() != null ? student.getDepartmentRef().getName() : student.getLegacyDepartment(),
                            student.getCourseRef() != null ? student.getCourseRef().getId() : null,
                            student.getCourseRef() != null ? student.getCourseRef().getName() : student.getLegacyCourse(),
                            student.getYear(),
                            student.getSemester(),
                            attendance != null ? toResponse(attendance, date) : null
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public AttendanceDtos.AttendanceResponse markAttendance(AttendanceDtos.AttendanceUpsertRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Student student = getStudentForTeacher(request.studentId(), teacher.getId());
        AttendanceSession session = getOrCreateDefaultSession(teacher, request.date(), student);

        attendanceEntryRepository.findByAttendanceSessionIdAndStudentId(session.getId(), student.getId())
                .ifPresent(existing -> { throw new BadRequestException("Attendance already marked for this student and date"); });

        AttendanceEntry attendance = new AttendanceEntry();
        attendance.setAttendanceSession(session);
        attendance.setStudent(student);
        attendance.setStatus(request.status());
        attendance.setCreatedBy(teacher.getEmail());
        attendance.setUpdatedBy(teacher.getEmail());
        return toResponse(attendanceEntryRepository.save(attendance), request.date());
    }

    @Override
    @Transactional
    public AttendanceDtos.AttendanceResponse updateAttendance(Long attendanceId, AttendanceDtos.AttendanceUpsertRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        AttendanceEntry attendance = attendanceEntryRepository.findByIdAndAttendanceSessionTeacherId(attendanceId, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
        Student student = getStudentForTeacher(request.studentId(), teacher.getId());
        AttendanceSession targetSession = getOrCreateDefaultSession(teacher, request.date(), student);
        attendanceEntryRepository.findByAttendanceSessionIdAndStudentId(targetSession.getId(), student.getId())
                .filter(existing -> !existing.getId().equals(attendanceId))
                .ifPresent(existing -> { throw new BadRequestException("Attendance already marked for this student and date"); });

        attendance.setAttendanceSession(targetSession);
        attendance.setStudent(student);
        attendance.setStatus(request.status());
        attendance.setUpdatedBy(teacher.getEmail());
        return toResponse(attendanceEntryRepository.save(attendance), request.date());
    }

    @Override
    public List<AttendanceDtos.AttendanceResponse> getAttendance(Long departmentId, Long courseId, LocalDate date) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Map<Long, AttendanceEntry> attendanceMap = getDefaultAttendanceMap(teacher.getId(), date, departmentId, courseId);
        return attendanceMap.values()
                .stream()
                .map(entry -> toResponse(entry, entry.getAttendanceSession().getSessionDate()))
                .toList();
    }

    @Override
    @Transactional
    public List<AttendanceDtos.AttendanceResponse> bulkMarkAttendance(AttendanceDtos.BulkAttendanceRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        return request.records().stream()
                .map(record -> upsertAttendance(record, teacher))
                .toList();
    }

    @Override
    @Transactional
    public AttendanceDtos.ClearAttendanceResponse clearAttendance(AttendanceDtos.ClearAttendanceRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        List<Long> scopedStudentIds = studentRepository.findAllByTeacherIdAndIdIn(teacher.getId(), request.studentIds())
                .stream()
                .map(Student::getId)
                .toList();

        if (scopedStudentIds.isEmpty()) {
            return new AttendanceDtos.ClearAttendanceResponse(0);
        }

        Map<Long, List<Long>> studentIdsBySession = new HashMap<>();
        for (Student student : studentRepository.findAllByTeacherIdAndIdIn(teacher.getId(), scopedStudentIds)) {
            attendanceSessionRepository.findDefaultSession(
                    teacher.getId(),
                    request.date(),
                    DEFAULT_SESSION_NAME,
                    student.getDepartmentRef() != null ? student.getDepartmentRef().getId() : null,
                    student.getCourseRef() != null ? student.getCourseRef().getId() : null
            ).ifPresent(session -> studentIdsBySession
                    .computeIfAbsent(session.getId(), key -> new ArrayList<>())
                    .add(student.getId()));
        }

        long clearedCount = 0;
        for (Map.Entry<Long, List<Long>> entry : studentIdsBySession.entrySet()) {
            clearedCount += attendanceEntryRepository.deleteByAttendanceSessionIdAndStudentIdIn(entry.getKey(), entry.getValue());
        }
        return new AttendanceDtos.ClearAttendanceResponse(clearedCount);
    }

    private AttendanceDtos.AttendanceResponse upsertAttendance(AttendanceDtos.AttendanceUpsertRequest request, Teacher teacher) {
        Student student = getStudentForTeacher(request.studentId(), teacher.getId());
        AttendanceSession session = getOrCreateDefaultSession(teacher, request.date(), student);
        AttendanceEntry attendance = attendanceEntryRepository
                .findByAttendanceSessionIdAndStudentId(session.getId(), student.getId())
                .orElseGet(AttendanceEntry::new);
        attendance.setAttendanceSession(session);
        attendance.setStudent(student);
        attendance.setStatus(request.status());
        if (attendance.getId() == null) {
            attendance.setCreatedBy(teacher.getEmail());
        }
        attendance.setUpdatedBy(teacher.getEmail());
        return toResponse(attendanceEntryRepository.save(attendance), request.date());
    }

    private Student getStudentForTeacher(Long studentId, Long teacherId) {
        return studentRepository.findByIdAndTeacherId(studentId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }

    private List<Student> findStudents(Long teacherId, Long departmentId, Long courseId) {
        if (departmentId != null && courseId != null) {
            return studentRepository.findAllByTeacherIdAndDepartmentRefIdAndCourseRefIdOrderByNameAsc(teacherId, departmentId, courseId);
        }
        if (departmentId != null) {
            return studentRepository.findAllByTeacherIdAndDepartmentRefIdOrderByNameAsc(teacherId, departmentId);
        }
        if (courseId != null) {
            return studentRepository.findAllByTeacherIdAndCourseRefIdOrderByNameAsc(teacherId, courseId);
        }
        return studentRepository.findAllByTeacherIdOrderByNameAsc(teacherId);
    }

    private AttendanceSession getOrCreateDefaultSession(Teacher teacher, LocalDate date, Student student) {
        Long departmentId = student.getDepartmentRef() != null ? student.getDepartmentRef().getId() : null;
        Long courseId = student.getCourseRef() != null ? student.getCourseRef().getId() : null;
        return attendanceSessionRepository.findDefaultSession(
                        teacher.getId(),
                        date,
                        DEFAULT_SESSION_NAME,
                        departmentId,
                        courseId
                )
                .orElseGet(() -> {
                    AttendanceSession session = new AttendanceSession();
                    session.setTeacher(teacher);
                    session.setDepartment(student.getDepartmentRef());
                    session.setCourse(student.getCourseRef());
                    session.setSessionDate(date);
                    session.setSessionName(DEFAULT_SESSION_NAME);
                    session.setCreatedBy(teacher.getEmail());
                    session.setUpdatedBy(teacher.getEmail());
                    return attendanceSessionRepository.save(session);
                });
    }

    private Map<Long, AttendanceEntry> getDefaultAttendanceMap(Long teacherId, LocalDate date, Long departmentId, Long courseId) {
        if (date == null) {
            return Map.of();
        }
        List<AttendanceSession> filteredSessions = attendanceSessionRepository.searchByDate(teacherId, date, departmentId, courseId, null, DEFAULT_SESSION_NAME);
        if (filteredSessions.isEmpty()) {
            return Map.of();
        }
        return attendanceEntryRepository.findAllByAttendanceSessionIdIn(
                        filteredSessions.stream().map(AttendanceSession::getId).toList()
                )
                .stream()
                .collect(Collectors.toMap(entry -> entry.getStudent().getId(), entry -> entry));
    }

    private AttendanceDtos.AttendanceResponse toResponse(AttendanceEntry attendance, LocalDate date) {
        return new AttendanceDtos.AttendanceResponse(
                attendance.getId(),
                attendance.getStudent().getId(),
                attendance.getStudent().getName(),
                attendance.getStudent().getRollNo(),
                date,
                attendance.getStatus()
        );
    }
}
