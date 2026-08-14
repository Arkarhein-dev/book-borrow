package com.startinpoint.lms.service;

import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.RequestContextFilter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if(overdueRecords.isEmpty()){
            log.info("No overdue Record Record Found Today...");
            return;
        }
        int emailSent = 0;
        Map<String, List<BorrowRecord>> overdueRecordsByUsers = overdueRecords.stream()
                .filter(record ->record.getUser() != null && record.getUser().getEmail() != null)
                .collect(Collectors.groupingBy(record -> record.getUser().getEmail()));

        for(Map.Entry<String,List<BorrowRecord>> entry : overdueRecordsByUsers.entrySet() ){
            String userEmail = entry.getKey();
            List<BorrowRecord> userBooks = entry.getValue();

            try {
                emailService.sendOverdueNotice(userEmail,userBooks);
                emailSent++;
            }catch (Exception e){
                log.error("Failed to send overdue email to user {}",userEmail,e);
            }
        }

        log.info("Overdue Check Completed. Sent {} total emails to overdue users.",emailSent);

    }
}
