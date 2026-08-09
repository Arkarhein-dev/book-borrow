package com.startinpoint.lms;


import com.startinpoint.lms.entity.Book;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.entity.UserRole;
import com.startinpoint.lms.repository.BookRepository;
import com.startinpoint.lms.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableAsync
public class LmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsApplication.class, args);
	}



//	@Bean
	public ApplicationRunner applicationRunner(BookRepository bookRepository) {
		return args -> {

			List<Book> books = List.of(
					new Book(null, "Clean Code", "Robert C. Martin", 10, true, null),
					new Book(null, "The Pragmatic Programmer", "Andrew Hunt, David Thomas", 8, true, null),
					new Book(null, "Design Patterns", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", 5, true, null),
					new Book(null, "Effective Java", "Joshua Bloch", 12, true, null),
					new Book(null, "Head First Design Patterns", "Eric Freeman, Elisabeth Robson", 0, false, null),
					new Book(null, "Refactoring", "Martin Fowler", 7, true, null),
					new Book(null, "Spring in Action", "Craig Walls", 15, true, null),
					new Book(null, "Spring Boot in Action", "Craig Walls", 6, true, null),
					new Book(null, "Pro Spring 6", "Iuliana Cosmina, Rob Harrop", 4, true, null),
					new Book(null, "Java Concurrency in Practice", "Brian Goetz", 0, false, null),
					new Book(null, "Introduction to Algorithms", "Thomas H. Cormen", 3, true, null),
					new Book(null, "Code Complete", "Steve McConnell", 9, true, null),
					new Book(null, "Domain-Driven Design", "Eric Evans", 2, true, null),
					new Book(null, "Microservices Patterns", "Chris Richardson", 11, true, null),
					new Book(null, "Continuous Delivery", "Jez Humble, David Farley", 5, true, null),
					new Book(null, "Building Microservices", "Sam Newman", 0, false, null),
					new Book(null, "Designing Data-Intensive Applications", "Martin Kleppmann", 14, true, null),
					new Book(null, "Database System Concepts", "Abraham Silberschatz", 4, true, null),
					new Book(null, "Clean Architecture", "Robert C. Martin", 10, true, null),
					new Book(null, "Test Driven Development", "Kent Beck", 6, true, null),
					new Book(null, "Soft Skills", "John Sonmez", 8, true, null),
					new Book(null, "Working Effectively with Legacy Code", "Michael Feathers", 1, true, null),
					new Book(null, "The Clean Coder", "Robert C. Martin", 0, false, null),
					new Book(null, "System Design Interview", "Alex Xu", 20, true, null),
					new Book(null, "Modern Java in Action", "Raoul-Gabriel Urma", 7, true, null),
					new Book(null, "Spring Data JPA in Action", "Catalin Tudose", 5, true, null),
					new Book(null, "Docker Deep Dive", "Nigel Poulton", 9, true, null),
					new Book(null, "Kubernetes Up & Running", "Brendan Burns, Joe Beda", 3, true, null),
					new Book(null, "Learning SQL", "Alan Beaulieu", 0, false, null),
					new Book(null, "Head First Java", "Kathy Sierra, Bert Bates", 13, true, null)
			);

			bookRepository.saveAll(books);
		};
	}
}
