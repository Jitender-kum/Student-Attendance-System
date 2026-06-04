package com.api.service;

import com.api.dto.StudentDtos;

import java.util.List;

public interface StudentService {

    List<StudentDtos.StudentResponse> getStudents(Long departmentId, Long courseId);

    StudentDtos.StudentResponse createStudent(StudentDtos.StudentRequest request);

    StudentDtos.StudentResponse updateStudent(Long id, StudentDtos.StudentRequest request);

    void deleteStudent(Long id);
}
