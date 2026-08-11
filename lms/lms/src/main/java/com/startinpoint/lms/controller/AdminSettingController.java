package com.startinpoint.lms.controller;

import com.startinpoint.lms.service.DynamicSchedulerService;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingController {
    private final DynamicSchedulerService dynamicSchedulerService;

    @GetMapping
    public String showSettingsPage(Model model){
        if(!model.containsAttribute("scheduleTime")){
            try{
                LocalTime activeTime = dynamicSchedulerService.getScheduledTime();
                model.addAttribute("scheduleTime", activeTime != null ? activeTime : LocalTime.of(0,0));
            }catch (SchedulerException e){
                model.addAttribute("scheduleTime",LocalTime.of(0,0));
            }
        }
        return "book/admin/settings";
    }

    @PostMapping("/scheduler")
    public String updateSchedulerTime(
            @RequestParam("time") @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            RedirectAttributes redirectAttributes
    ){
        try{
            dynamicSchedulerService.updateScheduleTime(time);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Console Text Job Schedule daily at " +time + "!");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to Schedule Job"+e.getMessage());
        }
        return "redirect:/admin/settings";
    }
}
