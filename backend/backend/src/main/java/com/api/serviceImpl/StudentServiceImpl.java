package com.api.serviceImpl;

import com.api.model.Student;
import com.api.repository.StudentRepository;
import com.api.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

//    @Override
//    public Student getStudentById(int id) {
//        return studentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Student not found"));
//    }

    @Override
    public Student saveStudent(Student student) {
        student.setStatus(true);
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Student student) {
        Optional<Student> existingOptional = studentRepository.findById(student.getId());
        if(existingOptional.isPresent()) {
            Student existing = existingOptional.get();
            existing.setName(student.getName());
            existing.setRollNo(student.getRollNo());
            existing.setDepartment(student.getDepartment());
            existing.setEmail(student.getEmail());
            existing.setPhoneNumber(student.getPhoneNumber());
            existing.setAddress(student.getAddress());
            existing.setFatherName(student.getFatherName());
            existing.setMotherName(student.getMotherName());
            existing.setFatherPhone(student.getFatherPhone());
            existing.setMotherPhone(student.getMotherPhone());
            existing.setCourse(student.getCourse());
            existing.setYear(student.getYear());
            existing.setStatus(true);
            existing.setAttendancePercentage(student.getAttendancePercentage());
            existing.setFatherOccupation(student.getFatherOccupation());
            existing.setUpdatedBy("admin");
            return studentRepository.save(existing);
        }
        return null;
    }

//    @Override
//    public void deleteStudent(int id) {
//        studentRepository.deleteById(id);
//    }
}