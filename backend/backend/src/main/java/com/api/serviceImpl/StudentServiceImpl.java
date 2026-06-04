package com.api.serviceImpl;

import com.api.dto.StudentDtos;
import com.api.exception.BadRequestException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.Course;
import com.api.model.Department;
import com.api.model.Student;
import com.api.model.Teacher;
import com.api.repository.AttendanceRepository;
import com.api.repository.AttendanceEntryRepository;
import com.api.repository.CourseRepository;
import com.api.repository.DepartmentRepository;
import com.api.repository.StudentRepository;
import com.api.security.AuthenticatedTeacherService;
import com.api.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceEntryRepository attendanceEntryRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final AuthenticatedTeacherService authenticatedTeacherService;

    public StudentServiceImpl(StudentRepository studentRepository,
                              AttendanceRepository attendanceRepository,
                              AttendanceEntryRepository attendanceEntryRepository,
                              DepartmentRepository departmentRepository,
                              CourseRepository courseRepository,
                              AuthenticatedTeacherService authenticatedTeacherService) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceEntryRepository = attendanceEntryRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.authenticatedTeacherService = authenticatedTeacherService;
    }

    @Override
    public List<StudentDtos.StudentResponse> getStudents(Long departmentId, Long courseId) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        return findStudents(teacher.getId(), departmentId, courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public StudentDtos.StudentResponse createStudent(StudentDtos.StudentRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        validateRollNumberForCreate(teacher.getId(), request.rollNumber());
        Department department = getDepartmentForTeacher(request.departmentId(), teacher.getId());
        Course course = getCourseForTeacher(request.courseId(), teacher.getId());
        validateCourseDepartment(course, department);

        Student student = new Student();
        applyRequest(student, request, teacher, department, course);
        student.setCreatedBy(teacher.getEmail());
        student.setUpdatedBy(teacher.getEmail());
        student.setAttendancePercentage(0.0);
        return toResponse(studentRepository.save(student));
    }

    @Override
    @Transactional
    public StudentDtos.StudentResponse updateStudent(Long id, StudentDtos.StudentRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        validateRollNumberForUpdate(teacher.getId(), request.rollNumber(), id);
        Department department = getDepartmentForTeacher(request.departmentId(), teacher.getId());
        Course course = getCourseForTeacher(request.courseId(), teacher.getId());
        validateCourseDepartment(course, department);
        Student student = studentRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        applyRequest(student, request, teacher, department, course);
        student.setUpdatedBy(teacher.getEmail());
        return toResponse(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Student student = studentRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        if (attendanceRepository.existsByStudentIdAndTeacherId(id, teacher.getId())
                || attendanceEntryRepository.existsByStudentIdAndAttendanceSessionTeacherId(id, teacher.getId())) {
            throw new BadRequestException("Cannot delete student while attendance records still exist");
        }
        studentRepository.delete(student);
    }

    private void applyRequest(Student student,
                              StudentDtos.StudentRequest request,
                              Teacher teacher,
                              Department department,
                              Course course) {
        student.setName(request.name().trim());
        student.setFatherName(trimToNull(request.fatherName()));
        student.setMotherName(trimToNull(request.motherName()));
        student.setRollNo(request.rollNumber().trim());
        student.setTeacher(teacher);
        student.setDepartmentRef(department);
        student.setCourseRef(course);
        student.setLegacyDepartment(department.getName());
        student.setLegacyCourse(course.getName());
        student.setEmail(request.email().trim());
        student.setPhoneNumber(request.phone().trim());
        student.setGender(request.gender().trim());
        student.setAddress(request.address().trim());
        student.setFatherPhone(trimToNull(request.fatherPhone()));
        student.setMotherPhone(trimToNull(request.motherPhone()));
        student.setFatherOccupation(trimToNull(request.fatherOccupation()));
        student.setYear(request.year());
        student.setSemester(request.semester());
        student.setStatus(request.status() == null ? Boolean.TRUE : request.status());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Department getDepartmentForTeacher(Long departmentId, Long teacherId) {
        return departmentRepository.findByIdAndTeacherId(departmentId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    private Course getCourseForTeacher(Long courseId, Long teacherId) {
        return courseRepository.findByIdAndTeacherId(courseId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private void validateCourseDepartment(Course course, Department department) {
        if (!course.getDepartment().getId().equals(department.getId())) {
            throw new BadRequestException("Course does not belong to the selected department");
        }
    }

    private void validateRollNumberForCreate(Long teacherId, String rollNumber) {
        if (studentRepository.existsByTeacherIdAndRollNoIgnoreCase(teacherId, rollNumber.trim())) {
            throw new BadRequestException("Roll number already exists");
        }
    }

    private void validateRollNumberForUpdate(Long teacherId, String rollNumber, Long id) {
        if (studentRepository.existsByTeacherIdAndRollNoIgnoreCaseAndIdNot(teacherId, rollNumber.trim(), id)) {
            throw new BadRequestException("Roll number already exists");
        }
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

    private StudentDtos.StudentResponse toResponse(Student student) {
        return new StudentDtos.StudentResponse(
                student.getId(),
                student.getName(),
                student.getFatherName(),
                student.getMotherName(),
                student.getRollNo(),
                student.getEmail(),
                student.getPhoneNumber(),
                student.getGender(),
                student.getAddress(),
                student.getFatherPhone(),
                student.getMotherPhone(),
                student.getFatherOccupation(),
                student.getYear(),
                student.getSemester(),
                student.getStatus(),
                student.getAttendancePercentage(),
                student.getDepartmentRef() != null ? student.getDepartmentRef().getId() : null,
                student.getDepartmentRef() != null ? student.getDepartmentRef().getName() : student.getLegacyDepartment(),
                student.getCourseRef() != null ? student.getCourseRef().getId() : null,
                student.getCourseRef() != null ? student.getCourseRef().getName() : student.getLegacyCourse()
        );
    }
}
