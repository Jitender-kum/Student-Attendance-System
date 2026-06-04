package com.api.service;

import com.api.dto.AuthDtos;

public interface AuthService {

    AuthDtos.AuthResponse signup(AuthDtos.SignupRequest request);

    AuthDtos.AuthResponse login(AuthDtos.LoginRequest request);
}
