package com.startinpoint.lms.job;

import com.startinpoint.lms.entity.Book;
import com.startinpoint.lms.repository.BookRepository;
import com.startinpoint.lms.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockOutCheckJob implements Job {
    private final EmailService emailService;
    private final BookRepository bookRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String adminEmail = context.getMergedJobDataMap().getString("adminEmail");

        if(adminEmail == null || adminEmail.isBlank()){
            log.warn("StockoutCheckJob skipped: Admin email is not provided.");
        }

        List<Book> stockOutBooks = bookRepository.findByStockEquals(0);

        if(!stockOutBooks.isEmpty()){
            log.info("Found {} out of stock books, sending notification to {}",stockOutBooks.size(),adminEmail);
            try{
                emailService.sentOutOfStockAlert(adminEmail,stockOutBooks);
                log.info("Successfully sent email to admin for stock out books");
            }catch (Exception e){
                log.error("Failed to sent out of stock book email to {}",adminEmail,e.getMessage());
            }
        }else{
            log.info("Stock out Check executed. All books are in stocks.");
        }
    }
}
