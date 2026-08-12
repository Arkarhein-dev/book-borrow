package com.startinpoint.lms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.standard.expression.MessageExpression;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;


    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.admin.email:norely@library.com}")
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
                            " Request Details:\n" +
                            "• User:      %s\n" +
                            "• Book:      %s\n" +
                            "• Book ID:   #%d\n\n" +
                            "Please consider restocking this item.\n\n" +
                            "—\n" +
                            "Library Management System",
                    requestUsername, bookTitle, bookId
            ));

            mailSender.send(message);
            log.info("Console message sent to Admin Successfully for Stock out....");
        } catch (Exception e) {
            log.error("Failed to send out of stock email: "+ e);
            throw new RuntimeException("Failed to send out of stock email....");
        }
    }

    public void sendOverdueNotice(String recipientEmail, String bookTitle, LocalDate dueDate) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
            helper.setFrom(adminEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("OVERDUE Notice : Please Return ' "+bookTitle+ "'");

            String htmlBody = buildOverdueEmailHtml(bookTitle,dueDate);
            helper.setText(htmlBody,true);

            mailSender.send(message);
            log.info("Overdue Email Alert Send Successfully to {}",recipientEmail);
        } catch (MessagingException e) {
            log.error("Failed To construct or send overdue email to {}",recipientEmail);
            throw new RuntimeException("Email Sending Failed ",e);
        }
    }

    private String buildOverdueEmailHtml(String bookTitle, LocalDate dueDate) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; color: #333333; line-height: 1.6;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #dddddd; border-radius: 8px;">
                    <h2 style="color: #d9534f; margin-top: 0;">Library Overdue Notice</h2>
                    <p>Dear My Library User,</p>
                    <p>This is an automated reminder that the following book borrowed from our library is past its due date:</p>
                    
                    <div style="background-color: #f8f9fa; padding: 15px; border-left: 4px solid #d9534f; margin: 20px 0;">
                        <p style="margin: 0; font-size: 16px;"><strong>Book Title:</strong> %s</p>
                        <p style="margin: 5px 0 0 0; font-size: 14px; color: #666666;"><strong>Due Date:</strong> %s</p>
                    </div>

                    <p>Please return this item to the library as soon as possible to avoid further late penalties.</p>
                    <p>If you have already returned this book, please disregard this email.</p>
                    
                    <hr style="border: none; border-top: 1px solid #eeeeee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #888888; text-align: center;">
                        Library Management System &bull; Automated Email Notification
                    </p>
                </div>
            </body>
            </html>
            """.formatted(bookTitle, dueDate.toString());
    }
}
