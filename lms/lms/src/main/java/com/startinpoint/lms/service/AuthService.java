package com.startinpoint.lms.service;

import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.entity.UserRole;
import com.startinpoint.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(User user){
        if(userRepository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("User Already Exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.USER);
        user.setActive(true);
        userRepository.save(user);
    }
}
