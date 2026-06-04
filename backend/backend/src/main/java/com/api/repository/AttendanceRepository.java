package com.api.repository;

import com.api.model.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<StudentAttendance,Long> {

    List<StudentAttendance> findByTeacherIdAndAttendanceDate(Long teacherId, LocalDate attendanceDate);

    Optional<StudentAttendance> findByStudentIdAndTeacherIdAndAttendanceDate(Long studentId, Long teacherId, LocalDate attendanceDate);

    Optional<StudentAttendance> findByIdAndTeacherId(Long id, Long teacherId);

    boolean existsByStudentIdAndTeacherId(Long studentId, Long teacherId);

    long deleteByTeacherIdAndAttendanceDateAndStudentIdIn(Long teacherId, LocalDate attendanceDate, List<Long> studentIds);

    @Query("""
            select sa
            from StudentAttendance sa
            where sa.teacher.id = :teacherId
              and (:departmentId is null or sa.student.departmentRef.id = :departmentId)
              and (:courseId is null or sa.student.courseRef.id = :courseId)
              and (:attendanceDate is null or sa.attendanceDate = :attendanceDate)
            order by sa.student.name asc
            """)
    List<StudentAttendance> searchAttendance(@Param("teacherId") Long teacherId,
                                             @Param("departmentId") Long departmentId,
                                             @Param("courseId") Long courseId,
                                             @Param("attendanceDate") LocalDate attendanceDate);

    @Query("""
            select sa
            from StudentAttendance sa
            where sa.teacher.id = :teacherId
              and sa.attendanceDate between :startDate and :endDate
              and (:departmentId is null or sa.student.departmentRef.id = :departmentId)
              and (:courseId is null or sa.student.courseRef.id = :courseId)
              and (:year is null or sa.student.year = :year)
              and (:semester is null or sa.student.semester = :semester)
            """)
    List<StudentAttendance> findForReport(@Param("teacherId") Long teacherId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("departmentId") Long departmentId,
                                          @Param("courseId") Long courseId,
                                          @Param("year") Integer year,
                                          @Param("semester") Integer semester);
}
