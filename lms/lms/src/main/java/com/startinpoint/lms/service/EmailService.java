package com.startinpoint.lms.service;

import com.startinpoint.lms.entity.Book;
import com.startinpoint.lms.entity.BorrowRecord;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.standard.expression.MessageExpression;

import java.time.LocalDate;
import java.util.List;

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

    public void sendOverdueNotice(String userEmail,List<BorrowRecord> userOverdueRecords) {
        if(userOverdueRecords.isEmpty()){
            return;
        }
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
            helper.setFrom(adminEmail);
            helper.setTo(userEmail);
            helper.setSubject("OVERDUE Notice : Please Return those books");

            String htmlBody = buildOverdueEmailHtml(userOverdueRecords);
            helper.setText(htmlBody,true);

            mailSender.send(message);
            log.info("Overdue Email Alert Send Successfully to {}",userEmail);
        } catch (MessagingException e) {
            log.error("Failed To construct or send overdue email to {}",userEmail);
            throw new RuntimeException("Email Sending Failed ",e);
        }
    }

    private String buildOverdueEmailHtml(List<BorrowRecord> userOverdueRecords) {
        StringBuilder tableRows = new StringBuilder();

        for(BorrowRecord record: userOverdueRecords){
            String title = (record.getBook().getTitle() != null) ? record.getBook().getTitle() : "Unknown Title";
            String dueDate = (record.getDueDate() != null) ? record.getDueDate().toString() : "N/A";

            tableRows.append(String.format("""
                    <tr>
                      <td style="padding: 10px; border-bottom: 1px solid #e0e0e0; font-weight: bold;">%s</td>
                      <td style="padding: 10px; border-bottom: 1px solid #e0e0e0; color: #d9534f;">%s</td>
                    </tr>
            """,title,dueDate));
        }
        return String.format("""
        <!DOCTYPE html>
        <html>
        <body style="font-family: Arial, sans-serif; color: #333333; line-height: 1.6;">
            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #dddddd; border-radius: 8px;">
                <h2 style="color: #d9534f; margin-top: 0;">Library Overdue Notice</h2>
                <p>Dear Library User,</p>
                <p>This is an automated reminder that you have <strong>%d overdue book(s)</strong> that need to be returned:</p>

                <table style="width: 100%%; border-collapse: collapse; margin: 20px 0; text-align: left;">
                    <thead>
                        <tr style="background-color: #f8f9fa;">
                            <th style="padding: 10px; border-bottom: 2px solid #cccccc;">Book Title</th>
                            <th style="padding: 10px; border-bottom: 2px solid #cccccc;">Due Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        %s
                    </tbody>
                </table>

                <p>Please return these items to the library as soon as possible to avoid further late penalties.</p>
                <p>If you have already returned these books, please disregard this email.</p>

                <hr style="border: none; border-top: 1px solid #eeeeee; margin: 20px 0;">
                <p style="font-size: 12px; color: #888888; text-align: center;">
                    Library Management System &bull; Automated Email Notification
                </p>
            </div>
        </body>
        </html>
    """, userOverdueRecords.size(), tableRows.toString());
    }

    public void sentOutOfStockAlert(String recipientEmail, List<Book> stockOutBooks) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"UTF-8");

        helper.setFrom(adminEmail);
        helper.setTo(recipientEmail);
        helper.setSubject("LMS Inventory Alert : Books Out of Stock...");
        String htmlContent = buildStockOutEmailHtml(stockOutBooks);
        helper.setText(htmlContent,true);
        mailSender.send(mimeMessage);
        log.info("Stock Out Email sent to {} Successfully... ",recipientEmail);
    }

    private String buildStockOutEmailHtml(List<Book> books) {
        StringBuilder tableRows = new StringBuilder();

        for (Book book : books) {
            tableRows.append(String.format("""
                <tr>
                    <td style="padding: 10px; border-bottom: 1px solid #e0e0e0;">%d</td>
                    <td style="padding: 10px; border-bottom: 1px solid #e0e0e0; font-weight: bold;">%s</td>
                    <td style="padding: 10px; border-bottom: 1px solid #e0e0e0;">%s</td>
                    <td style="padding: 10px; border-bottom: 1px solid #e0e0e0; color: #d9534f; font-weight: bold;">0</td>
                </tr>
            """, book.getId(), escapeHtml(book.getTitle()), escapeHtml(book.getAuthor())));
        }

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    <div style="border-bottom: 2px solid #d9534f; padding-bottom: 12px; margin-bottom: 20px;">
                        <h2 style="color: #d9534f; margin: 0;">Inventory Alert: Out of Stock</h2>
                    </div>
                    <p style="color: #333333; font-size: 15px;">
                        The dynamic scheduler check executed and found <strong>%d book(s)</strong> currently out of stock in the Library Management System:
                    </p>
        
                    <table style="width: 100%%; border-collapse: collapse; margin: 20px 0; text-align: left;">
                        <thead>
                            <tr style="background-color: #f8f9fa;">
                                <th style="padding: 10px; border-bottom: 2px solid #cccccc;">ID</th>
                                <th style="padding: 10px; border-bottom: 2px solid #cccccc;">Title</th>
                                <th style="padding: 10px; border-bottom: 2px solid #cccccc;">Author</th>
                                <th style="padding: 10px; border-bottom: 2px solid #cccccc;">Stock</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>

                    <p style="color: #666666; font-size: 13px; margin-top: 24px; border-top: 1px solid #eeeeee; padding-top: 12px;">
                        This is an automated notification sent by your configured LMS background scheduler.
                    </p>
                </div>
            </body>
            </html>
        """, books.size(), tableRows.toString());
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
