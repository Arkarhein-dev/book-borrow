package com.startinpoint.lms.mapper;

import com.startinpoint.lms.dto.request.UserCreateRequestDto;
import com.startinpoint.lms.dto.response.UserResponseDto;
import com.startinpoint.lms.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "role",ignore = true)
    @Mapping(target = "active",ignore = true)
    User toUserEntity(UserCreateRequestDto dto);

    UserResponseDto toResponseDto(User user);
}
