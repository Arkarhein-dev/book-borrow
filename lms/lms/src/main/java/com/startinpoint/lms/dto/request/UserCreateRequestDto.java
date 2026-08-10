package com.startinpoint.lms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequestDto(
        @NotBlank(message = "User name can't be blank")
        @Size(min = 3, max = 10, message = "Username must be between 3 and 10")
        String username,
        @NotBlank(message = "Email can't be blank")
        @Email(message = "Please Provide a valid Email address")
        String email,
        @NotBlank(message = "Password Can't be Blank")
        @Size(min = 5, message = "Password must be at least 5 character")
        String password
) {
}
