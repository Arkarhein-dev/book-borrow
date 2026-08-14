package com.startinpoint.lms.service;

import com.startinpoint.lms.dto.SchedulerConfigDto;
import com.startinpoint.lms.job.BorrowOverdueJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class QuartzSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(QuartzSchedulerService.class);
    private final Scheduler scheduler;

    public static final String JOB_KEY = "borrowOverdueJob";
    public static final String GROUP_KEY = "libraryJobs";
    public static final String TRIGGER_KEY = "borrowOverdueTrigger";
    public static final String DEFAULT_CRON = "0 0 0 * * ?"; // midnight daily

    @PostConstruct
    public void initScheduler(){
        try{
            JobKey jobKey = JobKey.jobKey(JOB_KEY,GROUP_KEY);
            if(!scheduler.checkExists(jobKey)){
                JobDetail jobDetail = JobBuilder
                        .newJob(BorrowOverdueJob.class)
                        .withIdentity(jobKey)
                        .storeDurably()
                        .build();

                CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                        .forJob(jobKey)
                        .withIdentity(TRIGGER_KEY,GROUP_KEY)
                        .withSchedule(CronScheduleBuilder.cronSchedule(DEFAULT_CRON)
                                .withMisfireHandlingInstructionFireAndProceed())
                        .build();

                scheduler.scheduleJob(jobDetail,cronTrigger);
                log.info("Initialized quartz job {} with default schedule {}",JOB_KEY,DEFAULT_CRON);
            }
        }catch (Exception e){
            log.error("Failed to Initialized Quartz Job scheduler.",e);
        }
    }

    public SchedulerConfigDto getOverdueJobConfig(){
        try{
            TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_KEY,GROUP_KEY);
            Trigger trigger = scheduler.getTrigger(triggerKey);

            boolean isEnabled = false;
            String cronExpression = DEFAULT_CRON;

            if(trigger != null){
                var state = scheduler.getTriggerState(triggerKey);
                isEnabled = (state !=Trigger.TriggerState.PAUSED);

                if(trigger instanceof CronTrigger cronTrigger){
                    cronExpression = cronTrigger.getCronExpression();
                }
            }
            return new SchedulerConfigDto(isEnabled,cronExpression);
        }catch (Exception e){
            log.error("Error Retrieving Quartz  Job configuration ",e);
            return new SchedulerConfigDto(false,DEFAULT_CRON);
        }
    }

    public void updateOverdueScheduler(boolean enabled, String cronExpression)throws SchedulerException{
        TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_KEY,GROUP_KEY);
        JobKey jobKey = JobKey.jobKey(JOB_KEY,GROUP_KEY);
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
                .withMisfireHandlingInstructionFireAndProceed();

        CronTrigger newTrigger = TriggerBuilder
                .newTrigger()
                .forJob(jobKey)
                .withIdentity(triggerKey)
                .withSchedule(scheduleBuilder)
                .build();

        scheduler.rescheduleJob(triggerKey,newTrigger);
        if(enabled){
            scheduler.resumeTrigger(triggerKey);
            log.info("Rescheduled and activated trigger '{}' with cron: {}",TRIGGER_KEY,cronExpression);
        }else{
            scheduler.pauseTrigger(triggerKey);
            log.info("Rescheduled and paused trigger '{}'",TRIGGER_KEY);
        }
    }

    public void triggerJobNow() throws SchedulerException{
        JobKey jobKey = JobKey.jobKey(JOB_KEY,GROUP_KEY);
        scheduler.triggerJob(jobKey);
        log.info("Manually trigger quartz Job '{}'",JOB_KEY);
    }

}
