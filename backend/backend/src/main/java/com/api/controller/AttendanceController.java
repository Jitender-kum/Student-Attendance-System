package com.api.controller;

import com.api.dto.StudentAttendanceResponseDto;
import com.api.model.StudentAttendance;
import com.api.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;


    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/get_student_attendance")
    public List<StudentAttendanceResponseDto> getStudentAttendanceList(@RequestParam("date") String date) {
        return attendanceService.getStudentAttendanceList(date);
    }

    @PostMapping("/mark")
    public String markAttendance(@RequestBody StudentAttendance studentAttendance) {
        return attendanceService.markAttendance(studentAttendance);
    }



}
