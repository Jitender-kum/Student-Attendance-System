package com.api.repository;

import com.api.model.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    Optional<AttendanceSession> findByIdAndTeacherId(Long id, Long teacherId);

    boolean existsBySubjectIdAndTeacherId(Long subjectId, Long teacherId);

    @Query("""
            select s
            from AttendanceSession s
            where s.teacher.id = :teacherId
              and s.sessionDate = :sessionDate
              and s.sessionName = :sessionName
              and s.subject is null
              and ((:departmentId is null and s.department is null) or s.department.id = :departmentId)
              and ((:courseId is null and s.course is null) or s.course.id = :courseId)
            """)
    Optional<AttendanceSession> findDefaultSession(@Param("teacherId") Long teacherId,
                                                   @Param("sessionDate") LocalDate sessionDate,
                                                   @Param("sessionName") String sessionName,
                                                   @Param("departmentId") Long departmentId,
                                                   @Param("courseId") Long courseId);

    @Query("""
            select s
            from AttendanceSession s
            where s.teacher.id = :teacherId
              and (:sessionDate is null or s.sessionDate = :sessionDate)
              and (:departmentId is null or s.department.id = :departmentId)
              and (:courseId is null or s.course.id = :courseId)
              and (:subjectId is null or s.subject.id = :subjectId)
              and (:sessionName is null or lower(s.sessionName) like lower(concat('%', :sessionName, '%')))
            order by s.sessionDate desc, s.sessionName asc
            """)
    List<AttendanceSession> searchByDate(@Param("teacherId") Long teacherId,
                                         @Param("sessionDate") LocalDate sessionDate,
                                         @Param("departmentId") Long departmentId,
                                         @Param("courseId") Long courseId,
                                         @Param("subjectId") Long subjectId,
                                         @Param("sessionName") String sessionName);

    @Query("""
            select s
            from AttendanceSession s
            where s.teacher.id = :teacherId
              and s.sessionDate between :startDate and :endDate
              and (:departmentId is null or s.department.id = :departmentId)
              and (:courseId is null or s.course.id = :courseId)
              and (:subjectId is null or s.subject.id = :subjectId)
              and (:sessionName is null or lower(s.sessionName) like lower(concat('%', :sessionName, '%')))
            order by s.sessionDate desc, s.sessionName asc
            """)
    List<AttendanceSession> searchByDateRange(@Param("teacherId") Long teacherId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              @Param("departmentId") Long departmentId,
                                              @Param("courseId") Long courseId,
                                              @Param("subjectId") Long subjectId,
                                              @Param("sessionName") String sessionName);
}
