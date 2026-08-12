package com.startinpoint.lms.job;

import com.startinpoint.lms.service.OverSchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class BorrowOverdueJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(BorrowOverdueJob.class);
    private final OverSchedulerService overSchedulerService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Quartz BorrowOverdueJob triggered automatically by scheduler.");
        try{
            overSchedulerService.processOverdueAlerts();
        }catch (Exception e){
            log.error("Error while processing overdue job ",e);
            throw new JobExecutionException("Execution Failed for BorrowRecord Over Due Job");
        }
    }
}
