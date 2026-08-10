package com.startinpoint.lms.dto.response;

import com.startinpoint.lms.entity.UserRole;
import lombok.Builder;

@Builder
public record UserResponseDto(
    Long id,
    String username,
    UserRole role
) {}
