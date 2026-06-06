package com.example.basketball.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.basketball.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:basketball@example.com}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendResetPasswordEmail(String to, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("AI篮球训练平台 - 密码重置");
        
        String content = String.format("尊敬的用户，\n\n您请求重置密码，请点击以下链接完成密码重置：\n\n%s\n\n该链接有效期为1小时，请及时使用。\n\n如果这不是您本人操作，请忽略此邮件。\n\nAI篮球训练平台团队", resetUrl);
        
        message.setText(content);
        
        try {
            if (fromEmail.contains("your_email") || fromEmail.contains("example")) {
                log.warn("测试环境：未配置真实邮箱，密码重置链接为: {}", resetUrl);
                return;
            }
            
            mailSender.send(message);
            log.info("密码重置邮件已发送至: {}", to);
        } catch (Exception e) {
            log.error("发送密码重置邮件失败: {}", e.getMessage());
            throw new RuntimeException("邮件发送失败，请稍后重试");
        }
    }
    
    public String getFrontendUrl() {
        return frontendUrl;
    }
}