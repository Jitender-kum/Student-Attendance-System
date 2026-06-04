package com.api.repository;

import com.api.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByTeacherIdOrderByNameAsc(Long teacherId);

    List<Course> findAllByTeacherIdAndDepartmentIdOrderByNameAsc(Long teacherId, Long departmentId);

    Optional<Course> findByIdAndTeacherId(Long id, Long teacherId);

    boolean existsByTeacherIdAndDepartmentId(Long teacherId, Long departmentId);

    boolean existsByTeacherIdAndCodeIgnoreCase(Long teacherId, String code);

    boolean existsByTeacherIdAndCodeIgnoreCaseAndIdNot(Long teacherId, String code, Long id);
}
