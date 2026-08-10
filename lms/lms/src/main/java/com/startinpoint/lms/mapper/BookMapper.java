package com.startinpoint.lms.mapper;

import com.startinpoint.lms.dto.request.BookCreateOrUpdateRequestDto;
import com.startinpoint.lms.dto.response.BookResponseDto;
import com.startinpoint.lms.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "available",expression = "java(dto.stock() != null && dto.stock() >0)")
    Book toBookEntity (BookCreateOrUpdateRequestDto dto);

    BookResponseDto toBookResponse(Book book);

}
