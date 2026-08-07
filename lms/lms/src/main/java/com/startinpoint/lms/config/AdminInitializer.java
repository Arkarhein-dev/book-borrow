package com.startinpoint.lms.config;

import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.entity.UserRole;
import com.startinpoint.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.admin.email}")
    private String adminEmail;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!userRepository.existsByRole(UserRole.ADMIN)){
            User admin = new User();
            admin.setActive(true);
            admin.setRole(UserRole.ADMIN);
            admin.setUsername("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("12345"));
            userRepository.save(admin);
        }
    }
}
