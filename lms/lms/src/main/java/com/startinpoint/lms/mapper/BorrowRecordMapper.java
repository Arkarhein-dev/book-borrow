package com.startinpoint.lms.mapper;

import com.startinpoint.lms.dto.response.BorrowRecordResponseDto;
import com.startinpoint.lms.entity.BorrowRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BorrowRecordMapper {
    @Mapping(target = "bookId",source = "book.id")
    @Mapping(target = "bookTitle",source = "book.title")
    @Mapping(target = "bookAuthor",source = "book.author")
    @Mapping(target = "userId",source = "user.id")
    @Mapping(target = "username",source = "user.username")
    BorrowRecordResponseDto toResponseDto(BorrowRecord borrowRecord);


}
