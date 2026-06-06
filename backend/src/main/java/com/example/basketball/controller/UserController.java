package com.example.basketball.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.basketball.config.BusinessException;
import com.example.basketball.dto.request.ForgotPasswordRequest;
import com.example.basketball.dto.request.LoginRequest;
import com.example.basketball.dto.request.RegisterRequest;
import com.example.basketball.dto.request.ResetPasswordRequest;
import com.example.basketball.dto.request.UserProfileRequest;
import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.dto.response.LoginResponse;
import com.example.basketball.dto.response.ResetTokenResponse;
import com.example.basketball.dto.response.UserProfileResponse;
import com.example.basketball.entity.User;
import com.example.basketball.service.LogService;
import com.example.basketball.service.UserService;
import com.example.basketball.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final LogService logService;

    public UserController(UserService userService, JwtUtil jwtUtil, LogService logService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.logService = logService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("注册成功", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        try {
            LoginResponse response = userService.login(request);
            Long userId = jwtUtil.getUserIdFromToken(response.getToken());
            String username = jwtUtil.getUsernameFromToken(response.getToken());
            logService.recordLoginLog(userId, username, ip, userAgent, "SUCCESS", null);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (RuntimeException e) {
            logService.recordLoginLog(null, request.getUsername(), ip, userAgent, "FAILED", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserProfileRequest request) {
        Long userId = extractUserId(token);
        UserProfileResponse profile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", profile));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("密码重置链接已发送至您的邮箱"));
    }

    @GetMapping("/reset-token/validate")
    public ResponseEntity<ApiResponse<ResetTokenResponse>> validateResetToken(String token) {
        boolean isValid = userService.validateResetToken(token);
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success(ResetTokenResponse.valid()));
        } else {
            return ResponseEntity.ok(ApiResponse.success(ResetTokenResponse.invalid("链接无效或已过期")));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("密码重置成功，请登录"));
    }

    private Long extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            return jwtUtil.getUserIdFromToken(jwtToken);
        }
        throw new BusinessException(401, "无效的认证令牌");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

}