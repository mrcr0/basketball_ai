package com.example.basketball.service;

public interface EmailService {

    void sendResetPasswordEmail(String to, String resetUrl);
}