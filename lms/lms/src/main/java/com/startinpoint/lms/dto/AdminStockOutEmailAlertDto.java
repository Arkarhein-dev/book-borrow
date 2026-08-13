package com.startinpoint.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminStockOutEmailAlertDto {

    private String adminEmail;
    private String triggerTime;
}
