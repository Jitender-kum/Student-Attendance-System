package com.api.serviceImpl;

import com.api.dto.DepartmentDtos;
import com.api.exception.BadRequestException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.Department;
import com.api.model.Teacher;
import com.api.repository.CourseRepository;
import com.api.repository.DepartmentRepository;
import com.api.repository.StudentRepository;
import com.api.security.AuthenticatedTeacherService;
import com.api.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final AuthenticatedTeacherService authenticatedTeacherService;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
                                 CourseRepository courseRepository,
                                 StudentRepository studentRepository,
                                 AuthenticatedTeacherService authenticatedTeacherService) {
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.authenticatedTeacherService = authenticatedTeacherService;
    }

    @Override
    @Transactional
    public DepartmentDtos.DepartmentResponse create(DepartmentDtos.DepartmentRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        if (departmentRepository.existsByTeacherIdAndNameIgnoreCase(teacher.getId(), request.name())) {
            throw new BadRequestException("Department name already exists");
        }
        Department department = new Department();
        department.setName(request.name().trim());
        department.setTeacher(teacher);
        return toResponse(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public DepartmentDtos.DepartmentResponse update(Long id, DepartmentDtos.DepartmentRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        if (departmentRepository.existsByTeacherIdAndNameIgnoreCaseAndIdNot(teacher.getId(), request.name(), id)) {
            throw new BadRequestException("Department name already exists");
        }
        Department department = departmentRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        department.setName(request.name().trim());
        return toResponse(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Department department = departmentRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        if (courseRepository.existsByTeacherIdAndDepartmentId(teacher.getId(), id)) {
            throw new BadRequestException("Cannot delete department while courses are still assigned to it");
        }
        if (studentRepository.existsByTeacherIdAndDepartmentRefId(teacher.getId(), id)) {
            throw new BadRequestException("Cannot delete department while students are still assigned to it");
        }
        departmentRepository.delete(department);
    }

    @Override
    public List<DepartmentDtos.DepartmentResponse> list() {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        return departmentRepository.findAllByTeacherIdOrderByNameAsc(teacher.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DepartmentDtos.DepartmentResponse toResponse(Department department) {
        return new DepartmentDtos.DepartmentResponse(department.getId(), department.getName());
    }
}
