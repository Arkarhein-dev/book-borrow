package com.startinpoint.lms.job;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConsoleTextJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(ConsoleTextJob.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("============================ QUARTZ SCHEDULAR EXECUTION ============================");
        log.info("======== Hello Admin! Quartz Job is executed successfully at "+ LocalDateTime.now());
        log.info("====================================================================================");
    }
}
