package com.api.repository;

import com.api.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByIdAndTeacherId(Long id, Long teacherId);

    @Query("""
            select n
            from Note n
            where n.teacher.id = :teacherId
              and (:departmentId is null or n.department.id = :departmentId)
              and (:courseId is null or n.course.id = :courseId)
              and (:noteDate is null or n.noteDate = :noteDate)
              and (:startDate is null or n.noteDate >= :startDate)
              and (:endDate is null or n.noteDate <= :endDate)
            order by n.noteDate desc, n.title asc
            """)
    List<Note> search(@Param("teacherId") Long teacherId,
                      @Param("departmentId") Long departmentId,
                      @Param("courseId") Long courseId,
                      @Param("noteDate") LocalDate noteDate,
                      @Param("startDate") LocalDate startDate,
                      @Param("endDate") LocalDate endDate);
}
