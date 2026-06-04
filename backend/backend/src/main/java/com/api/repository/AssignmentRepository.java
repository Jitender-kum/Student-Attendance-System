package com.api.repository;

import com.api.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByIdAndTeacherId(Long id, Long teacherId);

    @Query("""
            select a
            from Assignment a
            where a.teacher.id = :teacherId
              and (:departmentId is null or a.department.id = :departmentId)
              and (:courseId is null or a.course.id = :courseId)
              and (:dueDate is null or a.dueDate = :dueDate)
              and (:startDate is null or a.dueDate >= :startDate)
              and (:endDate is null or a.dueDate <= :endDate)
            order by a.dueDate asc, a.title asc
            """)
    List<Assignment> search(@Param("teacherId") Long teacherId,
                            @Param("departmentId") Long departmentId,
                            @Param("courseId") Long courseId,
                            @Param("dueDate") LocalDate dueDate,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate);
}
