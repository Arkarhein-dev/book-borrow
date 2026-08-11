package com.startinpoint.lms.service;

import com.startinpoint.lms.job.ConsoleTextJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DynamicSchedulerService {
    private final Scheduler scheduler;

    private static final String JOB_NAME ="consoleTextJob";
    private static final String TRIGGER_NAME = "consoleTextTrigger";
    private static final String GROUP_NAME = "adminJobs";

    public void updateScheduleTime(LocalTime executionTime) throws SchedulerException {
        JobKey jobKey = new JobKey(JOB_NAME,GROUP_NAME);
        TriggerKey triggerKey = new TriggerKey(TRIGGER_NAME,GROUP_NAME);

        String cronExpression = String.format("0 %d %d * * ?",executionTime.getMinute(), executionTime.getHour());

        // check schedule. If not, build
        if(!scheduler.checkExists(jobKey)){
            JobDetail jobDetail = JobBuilder
                    .newJob(ConsoleTextJob.class)
                    .withIdentity(jobKey)
                    .storeDurably()
                    .build();
            scheduler.addJob(jobDetail,true);
        }
    // Trigger build
        CronTrigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        if(scheduler.checkExists(triggerKey)){
            scheduler.rescheduleJob(triggerKey,newTrigger);
        }else{
            scheduler.scheduleJob(newTrigger);

        }
    }

    public LocalTime getScheduledTime() throws SchedulerException{
        TriggerKey triggerKey = new TriggerKey(TRIGGER_NAME,GROUP_NAME);
        Trigger trigger = scheduler.getTrigger(triggerKey);

        if(trigger instanceof CronTrigger cronTrigger){
            String cronExpression = cronTrigger.getCronExpression();
            String[] parts = cronExpression.split(" ");
            int minute = Integer.parseInt(parts[1]);
            int hour = Integer.parseInt(parts[2]);

            return LocalTime.of(hour,minute);
        }
        return null;
    }

}
