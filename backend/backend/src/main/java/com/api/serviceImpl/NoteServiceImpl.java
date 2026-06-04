package com.api.serviceImpl;

import com.api.dto.NoteDtos;
import com.api.exception.BadRequestException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.Course;
import com.api.model.Department;
import com.api.model.Note;
import com.api.model.Teacher;
import com.api.repository.CourseRepository;
import com.api.repository.DepartmentRepository;
import com.api.repository.NoteRepository;
import com.api.security.AuthenticatedTeacherService;
import com.api.service.NoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final AuthenticatedTeacherService authenticatedTeacherService;

    public NoteServiceImpl(NoteRepository noteRepository,
                           DepartmentRepository departmentRepository,
                           CourseRepository courseRepository,
                           AuthenticatedTeacherService authenticatedTeacherService) {
        this.noteRepository = noteRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.authenticatedTeacherService = authenticatedTeacherService;
    }

    @Override
    public List<NoteDtos.NoteResponse> list(Long departmentId, Long courseId, LocalDate noteDate, LocalDate startDate, LocalDate endDate) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        return noteRepository.search(teacher.getId(), departmentId, courseId, noteDate, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NoteDtos.NoteResponse create(NoteDtos.NoteRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Department department = getDepartment(request.departmentId(), teacher.getId());
        Course course = getCourse(request.courseId(), teacher.getId());
        validateCourseDepartment(course, department);
        Note note = new Note();
        apply(note, request, teacher, department, course);
        note.setCreatedBy(teacher.getEmail());
        note.setUpdatedBy(teacher.getEmail());
        return toResponse(noteRepository.save(note));
    }

    @Override
    @Transactional
    public NoteDtos.NoteResponse update(Long id, NoteDtos.NoteRequest request) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Note note = noteRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        Department department = getDepartment(request.departmentId(), teacher.getId());
        Course course = getCourse(request.courseId(), teacher.getId());
        validateCourseDepartment(course, department);
        apply(note, request, teacher, department, course);
        note.setUpdatedBy(teacher.getEmail());
        return toResponse(noteRepository.save(note));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Note note = noteRepository.findByIdAndTeacherId(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        noteRepository.delete(note);
    }

    private void apply(Note note, NoteDtos.NoteRequest request, Teacher teacher, Department department, Course course) {
        note.setTeacher(teacher);
        note.setDepartment(department);
        note.setCourse(course);
        note.setTitle(request.title().trim());
        note.setContent(request.content().trim());
        note.setNoteDate(request.noteDate());
    }

    private Department getDepartment(Long id, Long teacherId) {
        return departmentRepository.findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    private Course getCourse(Long id, Long teacherId) {
        return courseRepository.findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private void validateCourseDepartment(Course course, Department department) {
        if (course.getDepartment() != null && !course.getDepartment().getId().equals(department.getId())) {
            throw new BadRequestException("Course does not belong to the selected department");
        }
    }

    private NoteDtos.NoteResponse toResponse(Note note) {
        return new NoteDtos.NoteResponse(
                note.getId(),
                note.getDepartment() != null ? note.getDepartment().getId() : null,
                note.getDepartment() != null ? note.getDepartment().getName() : null,
                note.getCourse() != null ? note.getCourse().getId() : null,
                note.getCourse() != null ? note.getCourse().getName() : null,
                note.getTitle(),
                note.getContent(),
                note.getNoteDate()
        );
    }
}
