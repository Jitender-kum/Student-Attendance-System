package com.api.service;

import com.api.model.Student;
import java.util.List;

public interface StudentService {

    List<Student> getStudentList();

   // Student getStudentById(int id);

    Student saveStudent(Student student);

  //  void deleteStudent(int id);
//
    Student updateStudent(Student student);
}