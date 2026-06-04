package com.api.repository;

import com.api.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByIdAndTeacherId(Long id, Long teacherId);

    @Query("""
            select s
            from Subject s
            where s.teacher.id = :teacherId
              and (:departmentId is null or s.department.id = :departmentId)
              and (:courseId is null or s.course.id = :courseId)
              and (:search is null or lower(s.name) like lower(concat('%', :search, '%'))
                   or lower(coalesce(s.code, '')) like lower(concat('%', :search, '%')))
            order by s.name asc
            """)
    List<Subject> search(@Param("teacherId") Long teacherId,
                         @Param("departmentId") Long departmentId,
                         @Param("courseId") Long courseId,
                         @Param("search") String search);
}
