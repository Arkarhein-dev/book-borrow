package com.startinpoint.lms.config;

import com.startinpoint.lms.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueStatusesStartupRunner implements CommandLineRunner {
    private final BorrowRecordRepository borrowRecordRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking and updating overdue borrow records on startup...");
        int updateCount = borrowRecordRepository.updateOverdueStatusesOnStartUp();
        log.info("Startup overdue check completed. Updated {} record(s) to OVERDUE status.", updateCount);
    }
}
