package com.startinpoint.lms;


import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.entity.UserRole;
import com.startinpoint.lms.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class LmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsApplication.class, args);
	}

//	@Bean
	public ApplicationRunner runner(UserRepository userRepository, PasswordEncoder passwordEncoder){
		return r->{
			List<User> users = List.of(
					new User("User1",passwordEncoder.encode("12345"), UserRole.USER,true),
					new User("User2",passwordEncoder.encode("12345"), UserRole.USER,true),
					new User("User3",passwordEncoder.encode("12345"), UserRole.USER,true),
					new User("User4",passwordEncoder.encode("12345"), UserRole.USER,true),
					new User("User5",passwordEncoder.encode("12345"), UserRole.USER,true),
					new User("User6",passwordEncoder.encode("12345"), UserRole.USER,true),
					new User("User7",passwordEncoder.encode("12345"), UserRole.USER,true)
			);
			userRepository.saveAll(users);
		};
	}
}
