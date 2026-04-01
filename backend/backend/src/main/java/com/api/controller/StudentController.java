package com.api.controller;

import com.api.model.Student;
import com.api.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/list")
    public List<Student> getStudentList() {
        return studentService.getStudentList();
    }

//    @GetMapping("/{id}")
//    public Student getStudentById(@PathVariable int id) {
//        return studentService.getStudentById(id);
//    }

    @PostMapping("/add")
    public Student addStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    @PostMapping("/update")
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

//    @DeleteMapping("/delete/{id}")
//    public String deleteStudent(@PathVariable int id) {
//        studentService.deleteStudent(id);
//        return "Deleted Successfully";
//    }
}