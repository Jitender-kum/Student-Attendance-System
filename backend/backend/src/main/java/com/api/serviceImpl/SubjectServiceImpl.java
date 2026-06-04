package com.api.serviceImpl;

import com.api.dto.SubjectDtos;
import com.api.exception.BadRequestException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.Course;
import com.api.model.Department;
import com.api.model.Subject;
import com.api.model.Teacher;
import com.api.repository.CourseRepository;
import com.api.repository.DepartmentRepository;
import com.api.repository.SubjectRepository;
import com.api.repository.AttendanceSessionRepository;
import com.api.security.AuthenticatedTeacherService;
import com.api.service.SubjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AuthenticatedTeacherService authenticatedTeacherService;

    public SubjectServiceImpl(SubjectRepository subjectRepository,
                              DepartmentRepository departmentRepository,
                              CourseRepository courseRepository,
                              AttendanceSessionRepository attendanceSessionRepository,
                              AuthenticatedTeacherService authenticatedTeacherService) {
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.authenticatedTeacherService = authenticatedTeacherService;
    }

    @Override
    public List<SubjectDtos.SubjectResponse> list(Long departmentId, Long courseId, String search) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        return subjectRepository.search(teacher.getId(), departmentId, courseId, trimToNull(search))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SubjectDtos.SubjectResponse create(SubjectDtos.SubjectRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Subject subject = new Subject();
        apply(subject, request, teacher);
        subject.setCreatedBy(teacher.getEmail());
        subject.setUpdatedBy(teacher.getEmail());
        return toResponse(subjectRepository.save(subject));
    }

    @Override
    @Transactional
    public SubjectDtos.SubjectResponse update(Long id, SubjectDtos.SubjectRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Subject subject = subjectRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        apply(subject, request, teacher);
        subject.setUpdatedBy(teacher.getEmail());
        return toResponse(subjectRepository.save(subject));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Subject subject = subjectRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        if (attendanceSessionRepository.existsBySubjectIdAndTeacherId(subject.getId(), teacher.getId())) {
            throw new BadRequestException("Subject is linked to attendance sessions");
        }
        subjectRepository.delete(subject);
    }

    private void apply(Subject subject, SubjectDtos.SubjectRequest request, Teacher teacher) {
        Course course = courseRepository.findByIdAndTeacherId(request.courseId(), teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Department department = request.departmentId() == null
                ? course.getDepartment()
                : departmentRepository.findByIdAndTeacherId(request.departmentId(), teacher.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        if (course.getDepartment() != null && department != null && !course.getDepartment().getId().equals(department.getId())) {
            throw new BadRequestException("Course does not belong to the selected department");
        }

        subject.setTeacher(teacher);
        subject.setDepartment(department);
        subject.setCourse(course);
        subject.setName(request.name().trim());
        subject.setCode(trimToNull(request.code()));
        subject.setDescription(trimToNull(request.description()));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private SubjectDtos.SubjectResponse toResponse(Subject subject) {
        return new SubjectDtos.SubjectResponse(
                subject.getId(),
                subject.getDepartment() != null ? subject.getDepartment().getId() : null,
                subject.getDepartment() != null ? subject.getDepartment().getName() : null,
                subject.getCourse() != null ? subject.getCourse().getId() : null,
                subject.getCourse() != null ? subject.getCourse().getName() : null,
                subject.getName(),
                subject.getCode(),
                subject.getDescription()
        );
    }
}
