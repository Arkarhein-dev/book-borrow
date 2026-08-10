package com.startinpoint.lms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BorrowRecordRequestDto(
        @NotNull(message = "Book ID must not be null.")
        Long bookId
) {
}