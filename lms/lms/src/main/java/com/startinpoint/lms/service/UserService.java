package com.startinpoint.lms.service;

import com.startinpoint.lms.dto.response.UserResponseDto;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.mapper.UserMapper;
import com.startinpoint.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDto getUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found..."));
        return userMapper.toResponseDto(user);
    }
}
