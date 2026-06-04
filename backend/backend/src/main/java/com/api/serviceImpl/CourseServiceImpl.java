package com.api.serviceImpl;

import com.api.dto.CourseDtos;
import com.api.exception.BadRequestException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.Course;
import com.api.model.Department;
import com.api.model.Teacher;
import com.api.repository.CourseRepository;
import com.api.repository.DepartmentRepository;
import com.api.repository.StudentRepository;
import com.api.security.AuthenticatedTeacherService;
import com.api.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final AuthenticatedTeacherService authenticatedTeacherService;

    public CourseServiceImpl(CourseRepository courseRepository,
                             DepartmentRepository departmentRepository,
                             StudentRepository studentRepository,
                             AuthenticatedTeacherService authenticatedTeacherService) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
        this.authenticatedTeacherService = authenticatedTeacherService;
    }

    @Override
    @Transactional
    public CourseDtos.CourseResponse create(CourseDtos.CourseRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        validateCodeForCreate(teacher.getId(), request.code());
        Department department = getDepartmentForTeacher(request.departmentId(), teacher.getId());

        Course course = new Course();
        course.setName(request.name().trim());
        course.setCode(normalizeCode(request.code()));
        course.setDepartment(department);
        course.setTeacher(teacher);
        return toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseDtos.CourseResponse update(Long id, CourseDtos.CourseRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        validateCodeForUpdate(teacher.getId(), request.code(), id);
        Department department = getDepartmentForTeacher(request.departmentId(), teacher.getId());
        Course course = courseRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        course.setName(request.name().trim());
        course.setCode(normalizeCode(request.code()));
        course.setDepartment(department);
        course.setTeacher(teacher);
        return toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Course course = courseRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (studentRepository.existsByTeacherIdAndCourseRefId(teacher.getId(), id)) {
            throw new BadRequestException("Cannot delete course while students are still assigned to it");
        }
        courseRepository.delete(course);
    }

    @Override
    public List<CourseDtos.CourseResponse> list(Long departmentId) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        List<Course> courses = departmentId == null
                ? courseRepository.findAllByTeacherIdOrderByNameAsc(teacher.getId())
                : courseRepository.findAllByTeacherIdAndDepartmentIdOrderByNameAsc(teacher.getId(), departmentId);
        return courses.stream().map(this::toResponse).toList();
    }

    private Department getDepartmentForTeacher(Long departmentId, Long teacherId) {
        return departmentRepository.findByIdAndTeacherId(departmentId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    private void validateCodeForCreate(Long teacherId, String code) {
        String normalized = normalizeCode(code);
        if (normalized != null && courseRepository.existsByTeacherIdAndCodeIgnoreCase(teacherId, normalized)) {
            throw new BadRequestException("Course code already exists");
        }
    }

    private void validateCodeForUpdate(Long teacherId, String code, Long id) {
        String normalized = normalizeCode(code);
        if (normalized != null && courseRepository.existsByTeacherIdAndCodeIgnoreCaseAndIdNot(teacherId, normalized, id)) {
            throw new BadRequestException("Course code already exists");
        }
    }

    private String normalizeCode(String code) {
        return code == null || code.isBlank() ? null : code.trim().toUpperCase();
    }

    private CourseDtos.CourseResponse toResponse(Course course) {
        return new CourseDtos.CourseResponse(
                course.getId(),
                course.getName(),
                course.getCode(),
                course.getDepartment().getId(),
                course.getDepartment().getName()
        );
    }
}
