package com.example.basketball.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.basketball.config.BusinessException;
import com.example.basketball.dto.request.LoginRequest;
import com.example.basketball.dto.request.RegisterRequest;
import com.example.basketball.dto.request.UserProfileRequest;
import com.example.basketball.dto.response.LoginResponse;
import com.example.basketball.dto.response.UserProfileResponse;
import com.example.basketball.entity.PasswordResetToken;
import com.example.basketball.entity.User;
import com.example.basketball.mapper.PasswordResetTokenMapper;
import com.example.basketball.mapper.UserMapper;
import com.example.basketball.service.EmailService;
import com.example.basketball.service.UserService;
import com.example.basketball.util.JwtUtil;
import com.example.basketball.util.PasswordUtil;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final PasswordResetTokenMapper passwordResetTokenMapper;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public UserServiceImpl(UserMapper userMapper, PasswordUtil passwordUtil, JwtUtil jwtUtil,
                          EmailService emailService, PasswordResetTokenMapper passwordResetTokenMapper) {
        this.userMapper = userMapper;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.passwordResetTokenMapper = passwordResetTokenMapper;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordUtil.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("user");
        user.setStatus(1);

        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
        return user;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("账号已禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(86400000L);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setRole(user.getRole());
        userInfo.setPosition(user.getPosition());
        userInfo.setSkillLevel(user.getSkillLevel());
        response.setUser(userInfo);

        log.info("用户登录成功: {}", user.getUsername());
        return response;
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getHeight() != null) {
            user.setHeight(request.getHeight());
        }
        if (request.getWeight() != null) {
            user.setWeight(request.getWeight());
        }
        if (request.getPosition() != null) {
            user.setPosition(request.getPosition());
        }
        if (request.getExperienceYears() != null) {
            user.setExperienceYears(request.getExperienceYears());
        }
        if (request.getWeakPoints() != null) {
            user.setWeakPoints(request.getWeakPoints());
        }
        if (request.getTrainingGoal() != null) {
            user.setTrainingGoal(request.getTrainingGoal());
        }
        if (request.getSkillLevel() != null) {
            user.setSkillLevel(request.getSkillLevel());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userMapper.updateById(user);
        log.info("用户资料更新成功: {}", userId);
        return convertToProfileResponse(user);
    }

    @Override
    public User findById(Long userId) {
        return userMapper.selectById(userId);
    }

    private UserProfileResponse convertToProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setHeight(user.getHeight());
        response.setWeight(user.getWeight());
        response.setPosition(user.getPosition());
        response.setExperienceYears(user.getExperienceYears());
        response.setWeakPoints(user.getWeakPoints());
        response.setTrainingGoal(user.getTrainingGoal());
        response.setSkillLevel(user.getSkillLevel());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = findByEmail(email);
        if (user == null) {
            throw new BusinessException("该邮箱未注册");
        }

        passwordResetTokenMapper.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        
        PasswordResetToken resetToken = new PasswordResetToken(user.getId(), token, expiryDate);
        passwordResetTokenMapper.insert(resetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(email, resetUrl);

        log.info("密码重置链接已发送至邮箱: {}", email);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenMapper.selectByToken(token);
        
        if (resetToken == null) {
            throw new BusinessException("无效的重置链接");
        }
        
        if (resetToken.hasBeenUsed()) {
            throw new BusinessException("该链接已被使用");
        }
        
        if (resetToken.hasExpired()) {
            throw new BusinessException("链接已过期，请重新申请");
        }

        User user = userMapper.selectById(resetToken.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(passwordUtil.encode(newPassword));
        userMapper.updateById(user);

        passwordResetTokenMapper.markAsUsed(resetToken.getId());

        log.info("用户密码重置成功: {}", user.getUsername());
    }

    @Override
    public boolean validateResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenMapper.selectByToken(token);
        
        if (resetToken == null) {
            return false;
        }
        
        if (resetToken.hasBeenUsed()) {
            return false;
        }
        
        return !resetToken.hasExpired();
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

}
