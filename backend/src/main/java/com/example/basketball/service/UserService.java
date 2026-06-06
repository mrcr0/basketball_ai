package com.example.basketball.service;

import com.example.basketball.dto.request.LoginRequest;
import com.example.basketball.dto.request.RegisterRequest;
import com.example.basketball.dto.request.UserProfileRequest;
import com.example.basketball.dto.response.LoginResponse;
import com.example.basketball.dto.response.UserProfileResponse;
import com.example.basketball.entity.User;

public interface UserService {

    User register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserProfileResponse getProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UserProfileRequest request);

    User findById(Long userId);

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    boolean validateResetToken(String token);

    User findByEmail(String email);

}
