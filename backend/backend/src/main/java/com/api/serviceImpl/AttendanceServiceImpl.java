package com.api.serviceImpl;

import com.api.dto.StudentAttendanceResponseDto;
import com.api.model.Student;
import com.api.model.StudentAttendance;
import com.api.repository.AttendanceRepository;
import com.api.repository.StudentRepository;
import com.api.service.AttendanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<StudentAttendanceResponseDto> getStudentAttendanceList(String date) {

        List<StudentAttendanceResponseDto> list = new ArrayList<>();

        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }

        List<Student> studentList = studentRepository.findAll();

        if (studentList != null && !studentList.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                format.setLenient(false);

                Date parsedDate = format.parse(date);

                List<StudentAttendance> attendanceList =
                        attendanceRepository.findByAttendanceDate(parsedDate);

                Map<Long, StudentAttendance> attendanceMap =
                        (attendanceList != null && !attendanceList.isEmpty())
                                ? attendanceList.stream().collect(Collectors.toMap(
                                StudentAttendance::getStudentId,
                                x -> x
                        ))
                                : Collections.emptyMap();

                for (Student student : studentList) {

                    StudentAttendanceResponseDto dto = new StudentAttendanceResponseDto();

                    dto.setId(student.getId());
                    dto.setName(student.getName());
                    dto.setFatherName(student.getFatherName());
                    dto.setMotherName(student.getMotherName());
                    dto.setRollNo(student.getRollNo());
                    dto.setDepartment(student.getDepartment());
                    dto.setPhoneNumber(student.getPhoneNumber());
                    dto.setEmail(student.getEmail());
                    dto.setAddress(student.getAddress());
                    dto.setFatherPhone(student.getFatherPhone());
                    dto.setMotherPhone(student.getMotherPhone());
                    dto.setAttendancePercentage(student.getAttendancePercentage());
                    dto.setCourse(student.getCourse());
                    dto.setFatherOccupation(student.getFatherOccupation());
                    dto.setYear(student.getYear());
                    dto.setStatus(student.getStatus());
                    dto.setCreatedOn(student.getCreatedOn());
                    dto.setUpdatedOn(student.getUpdatedOn());
                    dto.setCreatedBy(student.getCreatedBy());
                    dto.setUpdatedBy(student.getUpdatedBy());

                    // Attendance mapping (same logic)
                    StudentAttendance attendance =
                            (!attendanceMap.isEmpty() && attendanceMap.containsKey(student.getId()))
                                    ? attendanceMap.get(student.getId())
                                    : null;

                    dto.setStudentAttendance(attendance);

                    list.add(dto);
                }

            } catch (ParseException e) {
                throw new RuntimeException("Invalid date format. Use yyyy-MM-dd");
            }
        }

        return list;
    }
    @Override
    public String markAttendance(StudentAttendance studentAttendance) {

        if (studentAttendance.getStudentId() == null || studentAttendance.getAttendanceDate() == null) {
            throw new IllegalArgumentException("StudentId and Date are required");
        }

        try {
            // Normalize date
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);

            Date normalizedDate = format.parse(format.format(studentAttendance.getAttendanceDate()));
            studentAttendance.setAttendanceDate(normalizedDate);

            // Check existing
            Optional<StudentAttendance> existing = attendanceRepository
                    .findByStudentIdAndAttendanceDate(studentAttendance.getStudentId(), normalizedDate);

            if (existing.isEmpty()) {
                // 🆕 INSERT
                studentAttendance.setCreatedOn(new Date());
                attendanceRepository.save(studentAttendance);

                return "Attendance marked";
            } else {
                // 🔁 UPDATE
                StudentAttendance old = existing.get();

                old.setAttendanceStatus(studentAttendance.getAttendanceStatus());
                old.setUpdatedBy(studentAttendance.getCreatedBy());
                old.setUpdatedOn(new Date());

                attendanceRepository.save(old);

                return "Attendance updated";
            }

        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
