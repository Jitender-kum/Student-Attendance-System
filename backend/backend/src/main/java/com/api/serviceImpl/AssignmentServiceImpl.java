package com.api.serviceImpl;

import com.api.dto.AssignmentDtos;
import com.api.exception.BadRequestException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.Assignment;
import com.api.model.Course;
import com.api.model.Department;
import com.api.model.Teacher;
import com.api.repository.AssignmentRepository;
import com.api.repository.CourseRepository;
import com.api.repository.DepartmentRepository;
import com.api.security.AuthenticatedTeacherService;
import com.api.service.AssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final AuthenticatedTeacherService authenticatedTeacherService;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository,
                                 DepartmentRepository departmentRepository,
                                 CourseRepository courseRepository,
                                 AuthenticatedTeacherService authenticatedTeacherService) {
        this.assignmentRepository = assignmentRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.authenticatedTeacherService = authenticatedTeacherService;
    }

    @Override
    public List<AssignmentDtos.AssignmentResponse> list(Long departmentId, Long courseId, LocalDate dueDate, LocalDate startDate, LocalDate endDate) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        return assignmentRepository.search(teacher.getId(), departmentId, courseId, dueDate, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AssignmentDtos.AssignmentResponse create(AssignmentDtos.AssignmentRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Department department = getDepartment(request.departmentId(), teacher.getId());
        Course course = getCourse(request.courseId(), teacher.getId());
        validateCourseDepartment(course, department);
        Assignment assignment = new Assignment();
        apply(assignment, request, teacher, department, course);
        assignment.setCreatedBy(teacher.getEmail());
        assignment.setUpdatedBy(teacher.getEmail());
        return toResponse(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public AssignmentDtos.AssignmentResponse update(Long id, AssignmentDtos.AssignmentRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Assignment assignment = assignmentRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        Department department = getDepartment(request.departmentId(), teacher.getId());
        Course course = getCourse(request.courseId(), teacher.getId());
        validateCourseDepartment(course, department);
        apply(assignment, request, teacher, department, course);
        assignment.setUpdatedBy(teacher.getEmail());
        return toResponse(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Assignment assignment = assignmentRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        assignmentRepository.delete(assignment);
    }

    private void apply(Assignment assignment, AssignmentDtos.AssignmentRequest request, Teacher teacher, Department department, Course course) {
        assignment.setTeacher(teacher);
        assignment.setDepartment(department);
        assignment.setCourse(course);
        assignment.setTitle(request.title().trim());
        assignment.setDescription(request.description() == null ? null : request.description().trim());
        assignment.setDueDate(request.dueDate());
    }

    private Department getDepartment(Long id, Long teacherId) {
        return departmentRepository.findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    private Course getCourse(Long id, Long teacherId) {
        return courseRepository.findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private void validateCourseDepartment(Course course, Department department) {
        if (course.getDepartment() != null && !course.getDepartment().getId().equals(department.getId())) {
            throw new BadRequestException("Course does not belong to the selected department");
        }
    }

    private AssignmentDtos.AssignmentResponse toResponse(Assignment assignment) {
        return new AssignmentDtos.AssignmentResponse(
                assignment.getId(),
                assignment.getDepartment() != null ? assignment.getDepartment().getId() : null,
                assignment.getDepartment() != null ? assignment.getDepartment().getName() : null,
                assignment.getCourse() != null ? assignment.getCourse().getId() : null,
                assignment.getCourse() != null ? assignment.getCourse().getName() : null,
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate()
        );
    }
}
