package com.api.security;

import com.api.exception.UnauthorizedException;
import com.api.model.Teacher;
import com.api.repository.TeacherRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedTeacherService {

    private final TeacherRepository teacherRepository;

    public AuthenticatedTeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public Teacher getCurrentTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return teacherRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Teacher account not found"));
    }
}
