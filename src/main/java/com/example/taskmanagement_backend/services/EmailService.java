package com.example.taskmanagement_backend.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async // gửi email bất đồng bộ
    public void sendInvitationEmail(String to, String projectName, String inviteLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Lời mời tham gia dự án: " + projectName);

        String content = String.format(
                        "Xin chào,\n\n" +
                        "Bạn được mời tham gia dự án '%s'.\n" +
                        "Vui lòng bấm vào liên kết sau để chấp nhận lời mời:\n%s\n\n" +
                        "Trân trọng,\nHệ thống Quản lý Dự án",
                projectName, inviteLink
        );

        message.setText(content);
        mailSender.send(message);
    }
}
