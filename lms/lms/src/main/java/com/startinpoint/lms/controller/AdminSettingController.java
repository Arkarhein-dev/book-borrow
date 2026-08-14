package com.startinpoint.lms.controller;

import com.startinpoint.lms.dto.SchedulerConfigDto;
import com.startinpoint.lms.dto.StockOutAlertConfigDto;
import com.startinpoint.lms.service.DynamicSchedulerService;
import com.startinpoint.lms.service.QuartzSchedulerService;
import com.startinpoint.lms.service.StockOutEmailTriggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingController {

    private final DynamicSchedulerService dynamicSchedulerService;
    private final QuartzSchedulerService quartzSchedulerService;
    private final StockOutEmailTriggerService stockOutEmailTriggerService;

    @GetMapping
    public String showSettingsPage(Model model){
        return "book/admin/settings";
    }

    @GetMapping("/show-borrow-overdue-scheduler-page")
    public String showBorrowOverdueSchedulerPage(Model model){
        SchedulerConfigDto config = quartzSchedulerService.getOverdueJobConfig();
        model.addAttribute("schedulerConfig", config);

        // Converts "0 30 14 * * ?" to "14:30" so the HTML <input type="time"> populates correctly
        model.addAttribute("scheduleTime", parseTimeFromCron(config.getCronExpression()));

        return "book/admin/borrow-overdue-scheduler";
    }

    @PostMapping("/save-borrow-overdue-scheduler")
    public String saveBorrowOverdueScheduler(
            @RequestParam(value = "enabled", defaultValue = "false") boolean enabled,
            @RequestParam("time") String time, // Accepts "HH:mm" from HTML
            RedirectAttributes redirectAttributes
    ){
        try {
            String[] timeParts = time.split(":");
            String hour = timeParts[0];
            String minute = timeParts[1];

            // Format into Quartz cron: "0 mm HH * * ?"
            String cronExpression = String.format("0 %s %s * * ?", minute, hour);

            quartzSchedulerService.updateOverdueScheduler(enabled, cronExpression);
            redirectAttributes.addFlashAttribute("successMessage", "Quartz overdue configuration updated successfully to " + time + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update Quartz schedule: " + e.getMessage());
        }
        return "redirect:/admin/settings/show-borrow-overdue-scheduler-page";
    }

    @PostMapping("/trigger-overdue-check")
    public String triggerOverdueCheckNow(RedirectAttributes redirectAttributes){
        try {
            quartzSchedulerService.triggerJobNow();
            redirectAttributes.addFlashAttribute("successMessage", "Quartz overdue alert job triggered immediately!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to trigger Quartz job now.");
        }

        return "redirect:/admin/settings/show-borrow-overdue-scheduler-page";
    }

    @GetMapping("/show-console-scheduler-page")
    public String showConsoleSchedulerPage(){
        return "book/admin/set-console-scheduler";
    }

    @PostMapping("/scheduler")
    public String updateSchedulerTime(
            @RequestParam("time") @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            RedirectAttributes redirectAttributes
    ){
        try {
            dynamicSchedulerService.updateScheduleTime(time);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Console Text Job Schedule daily at " + time + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to Schedule Job: " + e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    @GetMapping("/admin-email-alert-page")
    public String showAdminEmailAlertPage(
            Model model
    ){
        StockOutAlertConfigDto config = stockOutEmailTriggerService.getCurrentConfig();
        model.addAttribute("config",config);

        return "/book/admin/set-admin-email-alert-page";
    }

    @PostMapping("/admin-stock-out-alert")
    public String adminStockOutAlert(
            @ModelAttribute("config") StockOutAlertConfigDto config,
            RedirectAttributes redirectAttributes
    ){
        try{
            stockOutEmailTriggerService.updateStockOutSchedule(config);
            redirectAttributes.addFlashAttribute("successMessage","Stock Out email sent Succesfully.");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage","Failed to sent stock out email alert to admin...");
        }
        return "redirect:/admin/settings";
    }

    @PostMapping("/toggle-stock-out-alert")
    public String toggleStockOutAlert(
            @RequestParam(value = "enabled",defaultValue = "false")Boolean enabled
    ){
        try{
            stockOutEmailTriggerService.toggleAlertTrigger(enabled);
            String status = enabled ? "activated" : "paused";
        }catch (Exception e){
            System.err.print("Error while toggling stock out alert");
        }
        return "redirect:/admin/settings/admin-email-alert-page";
    }

    // Helper method to convert Quartz cron expression "0 mm HH * * ?" into "HH:mm"
    private String parseTimeFromCron(String cron) {
        try {
            if (cron == null || cron.trim().isEmpty()) {
                return "00:00";
            }
            String[] parts = cron.split(" ");
            int minute = Integer.parseInt(parts[1]);
            int hour = Integer.parseInt(parts[2]);
            return String.format("%02d:%02d", hour, minute);
        } catch (Exception e) {
            return "00:00"; // Default fallback time if parsing fails
        }
    }
}