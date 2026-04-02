package com.api.repository;

import com.api.model.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<StudentAttendance,Long> {

    List<StudentAttendance> findByAttendanceDate(Date parsedDate);

    Optional<StudentAttendance> findByStudentIdAndAttendanceDate(Long studentId, Date attendanceDate);
}
