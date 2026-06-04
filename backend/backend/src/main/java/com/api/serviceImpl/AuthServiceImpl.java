package com.api.serviceImpl;

import com.api.dto.AuthDtos;
import com.api.exception.BadRequestException;
import com.api.model.Teacher;
import com.api.repository.TeacherRepository;
import com.api.security.JwtService;
import com.api.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthServiceImpl(TeacherRepository teacherRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           UserDetailsService userDetailsService,
                           JwtService jwtService) {
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthDtos.AuthResponse signup(AuthDtos.SignupRequest request) {
        if (teacherRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BadRequestException("Email is already registered");
        }

        Teacher teacher = new Teacher();
        teacher.setFullName(request.fullName());
        teacher.setEmail(request.email().trim().toLowerCase());
        teacher.setPassword(passwordEncoder.encode(request.password()));
        Teacher savedTeacher = teacherRepository.save(teacher);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedTeacher.getEmail());
        return new AuthDtos.AuthResponse(
                jwtService.generateToken(userDetails),
                new AuthDtos.TeacherResponse(savedTeacher.getId(), savedTeacher.getFullName(), savedTeacher.getEmail())
        );
    }

    @Override
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        Teacher teacher = teacherRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(teacher.getEmail());
        return new AuthDtos.AuthResponse(
                jwtService.generateToken(userDetails),
                new AuthDtos.TeacherResponse(teacher.getId(), teacher.getFullName(), teacher.getEmail())
        );
    }
}
