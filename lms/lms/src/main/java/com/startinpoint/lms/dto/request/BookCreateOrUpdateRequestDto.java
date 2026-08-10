package com.startinpoint.lms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookCreateOrUpdateRequestDto(
        @NotBlank(message = "Title should not be blank.")
        String title,
        @NotBlank(message = "Author Name should not be blank")
        String author,
        @NotNull(message = "Stock should not be null.")
        @Min(value = 0, message = "Stock can't be negative.")
        Integer stock
) {
}
