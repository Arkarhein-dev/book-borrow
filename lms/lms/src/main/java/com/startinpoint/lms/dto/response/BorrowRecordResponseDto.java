package com.startinpoint.lms.dto.response;

import com.startinpoint.lms.entity.BorrowStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BorrowRecordResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        Long userId,
        String username,
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnedDate,
        BorrowStatus status
) {
}