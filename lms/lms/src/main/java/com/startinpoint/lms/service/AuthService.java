package com.startinpoint.lms.service;

import com.startinpoint.lms.dto.request.UserCreateRequestDto;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.entity.UserRole;
import com.startinpoint.lms.mapper.UserMapper;
import com.startinpoint.lms.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public void registerUser(UserCreateRequestDto dto){
        if(userRepository.existsByUsername(dto.username())){
            throw new IllegalArgumentException("User Already Exists");
        }

        if (userRepository.existsByEmail(dto.email())){
            throw new IllegalArgumentException("Email Already Exists.");
        }

        User user = userMapper.toUserEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(UserRole.USER);
        user.setActive(true);

        userRepository.save(user);
    }
}
