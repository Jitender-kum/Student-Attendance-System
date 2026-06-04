package com.api.repository;

import com.api.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByTeacherIdOrderByNameAsc(Long teacherId);

    Optional<Department> findByIdAndTeacherId(Long id, Long teacherId);

    boolean existsByTeacherIdAndNameIgnoreCase(Long teacherId, String name);

    boolean existsByTeacherIdAndNameIgnoreCaseAndIdNot(Long teacherId, String name, Long id);
}
