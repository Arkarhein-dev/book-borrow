package com.startinpoint.lms.dto.response;

import lombok.Builder;

@Builder
public record BookResponseDto(
    Long id,
    String title,
    String author,
    Integer stock,
    Boolean available
) {}
