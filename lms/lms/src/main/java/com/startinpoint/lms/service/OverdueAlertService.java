package com.startinpoint.lms.service;

import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OverdueAlertService {
    private static final Logger log = LoggerFactory.getLogger(OverdueAlertService.class);
    private final BorrowRecordRepository borrowRecordRepository;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public void processOverdueAlerts(){
        LocalDate today = LocalDate.now();
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueUnreturnedBooks(today);

        log.info("Starting Overdue check... Found {} overdue records",overdueRecords.size());
        int emailSents = 0;

        for(BorrowRecord record : overdueRecords){
            try{
                String recipientEmail = record.getUser().getEmail();
                String bookTitle = record.getBook().getTitle();
                LocalDate dueDate = record.getDueDate();

                emailService.sendOverdueNotice(recipientEmail, bookTitle, dueDate);
                emailSents++;
            }catch (Exception e){
                log.error("Failed to send overdue email for record ID {}",record.getId());
            }
        }
        log.info("Over Due Check Completed. Successfully Sent emails {}",emailSents);
    }
}
