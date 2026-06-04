package com.api.security;

import com.api.repository.TeacherRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TeacherUserDetailsService implements UserDetailsService {

    private final TeacherRepository teacherRepository;

    public TeacherUserDetailsService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return teacherRepository.findByEmailIgnoreCase(username)
                .map(teacher -> User.withUsername(teacher.getEmail())
                        .password(teacher.getPassword())
                        .authorities("ROLE_TEACHER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Teacher not found"));
    }
}
