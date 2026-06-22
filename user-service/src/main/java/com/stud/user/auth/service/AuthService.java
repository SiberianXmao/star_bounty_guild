package com.stud.user.auth.service;

import com.stud.user.auth.web.dto.AuthDtos.AuthResponse;
import com.stud.user.auth.web.dto.AuthDtos.LoginRequest;
import com.stud.user.auth.web.dto.AuthDtos.LogoutRequest;
import com.stud.user.auth.web.dto.AuthDtos.RefreshTokenRequest;
import com.stud.user.auth.web.dto.AuthDtos.RegisterRequest;
import com.stud.user.auth.web.dto.AuthDtos.UserSummary;
import org.springframework.security.core.Authentication;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(LogoutRequest request);

    UserSummary me(Authentication authentication);

    UserSummary me(String email);
}
