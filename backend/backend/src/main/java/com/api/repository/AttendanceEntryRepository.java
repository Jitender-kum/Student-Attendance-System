package com.api.repository;

import com.api.model.AttendanceEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceEntryRepository extends JpaRepository<AttendanceEntry, Long> {

    Optional<AttendanceEntry> findByIdAndAttendanceSessionTeacherId(Long id, Long teacherId);

    Optional<AttendanceEntry> findByAttendanceSessionIdAndStudentId(Long attendanceSessionId, Long studentId);

    List<AttendanceEntry> findAllByAttendanceSessionIdOrderByStudent_NameAsc(Long attendanceSessionId);

    List<AttendanceEntry> findAllByAttendanceSessionIdIn(List<Long> attendanceSessionIds);

    boolean existsByStudentIdAndAttendanceSessionTeacherId(Long studentId, Long teacherId);

    long deleteByAttendanceSessionIdAndStudentIdIn(Long attendanceSessionId, List<Long> studentIds);

    @Query("""
            select e
            from AttendanceEntry e
            join fetch e.attendanceSession s
            left join fetch s.department
            left join fetch s.course
            left join fetch s.subject
            join fetch e.student student
            where s.teacher.id = :teacherId
              and s.sessionDate between :startDate and :endDate
              and (:departmentId is null or s.department.id = :departmentId)
              and (:courseId is null or s.course.id = :courseId)
              and (:subjectId is null or s.subject.id = :subjectId)
              and (:year is null or student.year = :year)
              and (:semester is null or student.semester = :semester)
            """)
    List<AttendanceEntry> findForReport(@Param("teacherId") Long teacherId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("departmentId") Long departmentId,
                                        @Param("courseId") Long courseId,
                                        @Param("subjectId") Long subjectId,
                                        @Param("year") Integer year,
                                        @Param("semester") Integer semester);
}
