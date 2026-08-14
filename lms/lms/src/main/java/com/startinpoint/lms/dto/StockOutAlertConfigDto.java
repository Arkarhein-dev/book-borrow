package com.startinpoint.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockOutAlertConfigDto {
    private String adminEmail;
    private int intervalValue;
    private String  timeUnit;
    private boolean enabled;
}
