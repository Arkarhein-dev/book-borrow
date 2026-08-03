package com.startinpoint.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(form -> form.successHandler(successHandler));
        http.authorizeHttpRequests(
            auth-> auth
		        .requestMatchers("/books","/books/**").permitAll()
		        .requestMatchers("/logout").permitAll()
		        .requestMatchers("/admin/**").hasRole("ADMIN")
		        .requestMatchers("/user/**").hasRole("USER")
		        .anyRequest().authenticated()
        );
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) { 
        UserDetails admin = User.builder()
                .username("Admin")
                .password(passwordEncoder.encode("Admin123")) 
                .roles("ADMIN", "USER")
                .build();

        UserDetails user = User.builder()
                .username("User")
                .password(passwordEncoder.encode("User123"))  
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}