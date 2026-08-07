package com.startinpoint.lms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void sendOutOfStockNotificationToAdmin(
            String bookTitle, Long bookId, String requestUsername
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(adminEmail);
            message.setSubject("⚠️ Out of Stock Alert: " + bookTitle);

            message.setText(String.format(
                    "Hello Admin,\n\n" +
                            "A user attempted to borrow a book that is currently out of stock.\n\n" +
                            "📌 Request Details:\n" +
                            "• User:      %s\n" +
                            "• Book:      %s\n" +
                            "• Book ID:   #%d\n\n" +
                            "Please consider restocking this item.\n\n" +
                            "—\n" +
                            "Library Management System",
                    requestUsername, bookTitle, bookId
            ));

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send out of stock email: " + e.getMessage());
        }
    }

}
