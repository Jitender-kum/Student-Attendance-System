package com.api.serviceImpl;

import com.api.dto.AttendanceSessionDtos;
import com.api.exception.BadRequestException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.*;
import com.api.repository.AttendanceEntryRepository;
import com.api.repository.AttendanceSessionRepository;
import com.api.repository.CourseRepository;
import com.api.repository.DepartmentRepository;
import com.api.repository.StudentRepository;
import com.api.repository.SubjectRepository;
import com.api.security.AuthenticatedTeacherService;
import com.api.service.AttendanceSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceSessionServiceImpl implements AttendanceSessionService {

    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceEntryRepository attendanceEntryRepository;
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final AuthenticatedTeacherService authenticatedTeacherService;

    public AttendanceSessionServiceImpl(AttendanceSessionRepository attendanceSessionRepository,
                                        AttendanceEntryRepository attendanceEntryRepository,
                                        StudentRepository studentRepository,
                                        DepartmentRepository departmentRepository,
                                        CourseRepository courseRepository,
                                        SubjectRepository subjectRepository,
                                        AuthenticatedTeacherService authenticatedTeacherService) {
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.attendanceEntryRepository = attendanceEntryRepository;
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
        this.authenticatedTeacherService = authenticatedTeacherService;
    }

    @Override
    @Transactional
    public AttendanceSessionDtos.AttendanceSessionResponse createSession(AttendanceSessionDtos.AttendanceSessionRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        AttendanceSession session = new AttendanceSession();
        applySession(session, request, teacher);
        session.setCreatedBy(teacher.getEmail());
        session.setUpdatedBy(teacher.getEmail());
        return toSessionResponse(attendanceSessionRepository.save(session));
    }

    @Override
    public List<AttendanceSessionDtos.AttendanceSessionResponse> listSessionsByDate(LocalDate date, Long departmentId, Long courseId, Long subjectId, String sessionName) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        return attendanceSessionRepository.searchByDate(teacher.getId(), date, departmentId, courseId, subjectId, trimToNull(sessionName))
                .stream()
                .map(this::toSessionResponse)
                .toList();
    }

    @Override
    public List<AttendanceSessionDtos.AttendanceSessionResponse> listSessionsByDateRange(LocalDate startDate, LocalDate endDate, Long departmentId, Long courseId, Long subjectId, String sessionName) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        return attendanceSessionRepository.searchByDateRange(teacher.getId(), startDate, endDate, departmentId, courseId, subjectId, trimToNull(sessionName))
                .stream()
                .map(this::toSessionResponse)
                .toList();
    }

    @Override
    public AttendanceSessionDtos.AttendanceSessionDetailResponse getSession(Long sessionId) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        AttendanceSession session = getSessionForTeacher(sessionId, teacher.getId());
        return toSessionDetail(session);
    }

    @Override
    @Transactional
    public AttendanceSessionDtos.AttendanceSessionResponse updateSession(Long sessionId, AttendanceSessionDtos.AttendanceSessionRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        AttendanceSession session = getSessionForTeacher(sessionId, teacher.getId());
        applySession(session, request, teacher);
        session.setUpdatedBy(teacher.getEmail());
        return toSessionResponse(attendanceSessionRepository.save(session));
    }

    @Override
    @Transactional
    public AttendanceSessionDtos.AttendanceEntryResponse updateEntry(Long sessionId, Long entryId, AttendanceSessionDtos.AttendanceEntryUpdateRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        AttendanceSession session = getSessionForTeacher(sessionId, teacher.getId());
        AttendanceEntry entry = attendanceEntryRepository.findByIdAndAttendanceSessionTeacherId(entryId, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Attendance entry not found"));
        if (!entry.getAttendanceSession().getId().equals(session.getId())) {
            throw new ResourceNotFoundException("Attendance entry not found");
        }
        entry.setStatus(request.status());
        entry.setUpdatedBy(teacher.getEmail());
        AttendanceEntry saved = attendanceEntryRepository.save(entry);
        return toEntryResponse(saved, saved.getStudent());
    }

    @Override
    @Transactional
    public AttendanceSessionDtos.AttendanceSessionDetailResponse bulkUpsertEntries(Long sessionId, AttendanceSessionDtos.AttendanceSessionEntryBulkRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        AttendanceSession session = getSessionForTeacher(sessionId, teacher.getId());
        for (AttendanceSessionDtos.AttendanceEntryUpsertRequest record : request.entries()) {
            Student student = studentRepository.findByIdAndTeacherId(record.studentId(), teacher.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            AttendanceEntry entry = attendanceEntryRepository.findByAttendanceSessionIdAndStudentId(session.getId(), student.getId())
                    .orElseGet(AttendanceEntry::new);
            entry.setAttendanceSession(session);
            entry.setStudent(student);
            entry.setStatus(record.status());
            if (entry.getId() == null) {
                entry.setCreatedBy(teacher.getEmail());
            }
            entry.setUpdatedBy(teacher.getEmail());
            attendanceEntryRepository.save(entry);
        }
        return toSessionDetail(session);
    }

    @Override
    @Transactional
    public void deleteSession(Long sessionId) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        AttendanceSession session = getSessionForTeacher(sessionId, teacher.getId());
        attendanceSessionRepository.delete(session);
    }

    private AttendanceSession getSessionForTeacher(Long sessionId, Long teacherId) {
        return attendanceSessionRepository.findByIdAndTeacherId(sessionId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance session not found"));
    }

    private void applySession(AttendanceSession session, AttendanceSessionDtos.AttendanceSessionRequest request, Teacher teacher) {
        Department department = request.departmentId() == null ? null : departmentRepository.findByIdAndTeacherId(request.departmentId(), teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Course course = request.courseId() == null ? null : courseRepository.findByIdAndTeacherId(request.courseId(), teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Subject subject = request.subjectId() == null ? null : subjectRepository.findByIdAndTeacherId(request.subjectId(), teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        if (department != null && course != null && course.getDepartment() != null && !course.getDepartment().getId().equals(department.getId())) {
            throw new BadRequestException("Course does not belong to the selected department");
        }
        if (subject != null) {
            if (course == null) {
                course = subject.getCourse();
            }
            if (department == null) {
                department = subject.getDepartment();
            }
            if (subject.getCourse() != null && course != null && !subject.getCourse().getId().equals(course.getId())) {
                throw new BadRequestException("Subject does not belong to the selected course");
            }
            if (subject.getDepartment() != null && department != null && !subject.getDepartment().getId().equals(department.getId())) {
                throw new BadRequestException("Subject does not belong to the selected department");
            }
        }
        session.setTeacher(teacher);
        session.setDepartment(department);
        session.setCourse(course);
        session.setSubject(subject);
        session.setSessionDate(request.sessionDate());
        session.setSessionName(request.sessionName().trim());
        session.setSubjectOrTopic(trimToNull(request.subjectOrTopic()));
        session.setRemarks(trimToNull(request.remarks()));
        session.setStartTime(request.startTime());
        session.setEndTime(request.endTime());
        session.setPeriodLabel(trimToNull(request.periodLabel()));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private AttendanceSessionDtos.AttendanceSessionResponse toSessionResponse(AttendanceSession session) {
        return new AttendanceSessionDtos.AttendanceSessionResponse(
                session.getId(),
                session.getDepartment() != null ? session.getDepartment().getId() : null,
                session.getDepartment() != null ? session.getDepartment().getName() : null,
                session.getCourse() != null ? session.getCourse().getId() : null,
                session.getCourse() != null ? session.getCourse().getName() : null,
                session.getSubject() != null ? session.getSubject().getId() : null,
                session.getSubject() != null ? session.getSubject().getName() : null,
                session.getSessionDate(),
                session.getSessionName(),
                session.getSubjectOrTopic(),
                session.getRemarks(),
                session.getStartTime(),
                session.getEndTime(),
                session.getPeriodLabel(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    private AttendanceSessionDtos.AttendanceSessionDetailResponse toSessionDetail(AttendanceSession session) {
        List<AttendanceEntry> savedEntries = attendanceEntryRepository.findAllByAttendanceSessionIdOrderByStudent_NameAsc(session.getId());
        Map<Long, AttendanceEntry> entryByStudentId = savedEntries.stream()
                .collect(Collectors.toMap(entry -> entry.getStudent().getId(), entry -> entry));
        List<Student> students = findStudents(
                session.getTeacher().getId(),
                session.getDepartment() != null ? session.getDepartment().getId() : null,
                session.getCourse() != null ? session.getCourse().getId() : null
        );
        List<AttendanceSessionDtos.AttendanceEntryResponse> entries = students
                .stream()
                .sorted(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER))
                .map(student -> toEntryResponse(entryByStudentId.get(student.getId()), student))
                .toList();
        return new AttendanceSessionDtos.AttendanceSessionDetailResponse(
                session.getId(),
                session.getDepartment() != null ? session.getDepartment().getId() : null,
                session.getDepartment() != null ? session.getDepartment().getName() : null,
                session.getCourse() != null ? session.getCourse().getId() : null,
                session.getCourse() != null ? session.getCourse().getName() : null,
                session.getSubject() != null ? session.getSubject().getId() : null,
                session.getSubject() != null ? session.getSubject().getName() : null,
                session.getSessionDate(),
                session.getSessionName(),
                session.getSubjectOrTopic(),
                session.getRemarks(),
                session.getStartTime(),
                session.getEndTime(),
                session.getPeriodLabel(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                entries
        );
    }

    private AttendanceSessionDtos.AttendanceEntryResponse toEntryResponse(AttendanceEntry entry, Student student) {
        return new AttendanceSessionDtos.AttendanceEntryResponse(
                entry != null ? entry.getId() : null,
                student.getId(),
                student.getName(),
                student.getRollNo(),
                student.getDepartmentRef() != null ? student.getDepartmentRef().getId() : null,
                student.getDepartmentRef() != null ? student.getDepartmentRef().getName() : student.getLegacyDepartment(),
                student.getCourseRef() != null ? student.getCourseRef().getId() : null,
                student.getCourseRef() != null ? student.getCourseRef().getName() : student.getLegacyCourse(),
                student.getYear(),
                student.getSemester(),
                entry != null ? entry.getStatus() : null,
                entry != null ? entry.getMarkedAt() : null,
                entry != null ? entry.getUpdatedAt() : null
        );
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
}
