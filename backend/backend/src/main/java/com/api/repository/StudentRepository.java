package com.api.repository;

import com.api.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByTeacherIdOrderByNameAsc(Long teacherId);

    List<Student> findAllByTeacherIdAndDepartmentRefIdOrderByNameAsc(Long teacherId, Long departmentId);

    List<Student> findAllByTeacherIdAndCourseRefIdOrderByNameAsc(Long teacherId, Long courseId);

    List<Student> findAllByTeacherIdAndDepartmentRefIdAndCourseRefIdOrderByNameAsc(Long teacherId, Long departmentId, Long courseId);

    List<Student> findAllByTeacherIdAndIdIn(Long teacherId, List<Long> ids);

    Optional<Student> findByIdAndTeacherId(Long id, Long teacherId);

    boolean existsByTeacherIdAndDepartmentRefId(Long teacherId, Long departmentId);

    boolean existsByTeacherIdAndCourseRefId(Long teacherId, Long courseId);

    boolean existsByTeacherIdAndRollNoIgnoreCase(Long teacherId, String rollNo);

    boolean existsByTeacherIdAndRollNoIgnoreCaseAndIdNot(Long teacherId, String rollNo, Long id);
}
