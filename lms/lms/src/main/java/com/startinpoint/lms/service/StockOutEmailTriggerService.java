package com.startinpoint.lms.service;

import com.startinpoint.lms.dto.StockOutAlertConfigDto;
import com.startinpoint.lms.job.StockOutCheckJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockOutEmailTriggerService {
    private final Scheduler scheduler;

    private static final String JOB_KEY = "stockOutCheckJob";
    private static final String TRIGGER_KEY = "stockOutCheckTrigger";
    private static final String GROUP_KEY = "stockOutCheckGroup";

    public void updateStockOutSchedule(@NonNull StockOutAlertConfigDto dto) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(JOB_KEY,GROUP_KEY);
        TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_KEY,GROUP_KEY);

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("adminEmail",dto.getAdminEmail());
        jobDataMap.put("intervalValue",dto.getIntervalValue());
        jobDataMap.put("timeUnit",dto.getTimeUnit());

            JobDetail jobDetail = JobBuilder
                    .newJob(StockOutCheckJob.class)
                    .withIdentity(jobKey)
                    .setJobData(jobDataMap)
                    .storeDurably()
                    .build();

            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().repeatForever();
            if("HOURS".equalsIgnoreCase(dto.getTimeUnit())){
                scheduleBuilder.withIntervalInHours(dto.getIntervalValue());
            }else{
                scheduleBuilder.withIntervalInMinutes(dto.getIntervalValue());
            }

            Trigger newTrigger = TriggerBuilder
                    .newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(triggerKey)
                    .usingJobData(jobDataMap)
                    .withSchedule(scheduleBuilder)
                    .build();

            Boolean wasPaused = false;
            if(scheduler.checkExists(jobKey)){
                scheduler.addJob(jobDetail,true);
            }else{
                scheduler.addJob(jobDetail,false);
            }

            if(scheduler.checkExists(triggerKey)){
                scheduler.rescheduleJob(triggerKey,newTrigger);
            }else{
                scheduler.scheduleJob(newTrigger);
            }

        // Preserve existing pause/active state instead of relying on dto.isEnabled()
        if (wasPaused) {
            scheduler.pauseTrigger(triggerKey);
            log.info("Stock out alert updated (maintained PAUSED state).");
        } else {
            log.info("Stock out alert updated (maintained ACTIVE state).");
        }

    }


    public void toggleAlertTrigger(Boolean enabled) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_KEY, GROUP_KEY);

        if (scheduler.checkExists(triggerKey)) {
            if (Boolean.TRUE.equals(enabled)) {
                scheduler.resumeTrigger(triggerKey);
                log.info("Stock out alert trigger resumed.");
            } else {
                scheduler.pauseTrigger(triggerKey);
                log.info("Stock out alert trigger paused.");
            }
        } else {
            log.warn("Cannot toggle status: Stock out alert trigger does not exist yet. Please configure and save a schedule first.");
            throw new IllegalStateException("Trigger does not exist. Please configure schedule first.");
        }
    }



    public StockOutAlertConfigDto getCurrentConfig()  {
        TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_KEY, GROUP_KEY);
        JobKey jobKey = JobKey.jobKey(JOB_KEY, GROUP_KEY);

        StockOutAlertConfigDto dto = new StockOutAlertConfigDto();
        try {
            if (scheduler.checkExists(jobKey)) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                JobDataMap jobDataMap = jobDetail.getJobDataMap();

                dto.setAdminEmail(jobDataMap.getString("adminEmail"));
                dto.setIntervalValue(jobDataMap.getInt("intervalValue"));
                dto.setTimeUnit(jobDataMap.getString("timeUnit"));
            }

            if (scheduler.checkExists(triggerKey)) {
                Trigger.TriggerState state = scheduler.getTriggerState(triggerKey);
                // If state is NORMAL, it is ACTIVE. If PAUSED, it is disabled.
                dto.setEnabled(state == Trigger.TriggerState.NORMAL);
            } else {
                dto.setEnabled(false);
            }
        }catch (Exception e){
            log.error("Error while getting current config.");
        }

        return dto;
    }
}
