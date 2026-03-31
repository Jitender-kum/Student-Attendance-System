package com.api.serviceImpl;

import com.api.model.Student;
import com.api.repository.StudentRepository;
import com.api.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> getStudentList() {
        return studentRepository.findAll();
    }
}
