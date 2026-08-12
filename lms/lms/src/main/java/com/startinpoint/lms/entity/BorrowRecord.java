package com.startinpoint.lms.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY,optional = false)
	@JoinColumn(name = "user_id",nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY,optional = false)
	@JoinColumn(name = "book_id",nullable = false)
	private Book book;
	
	@Column(nullable = false)
	private LocalDate borrowDate;
	
	@Column(nullable = false)
	private LocalDate dueDate;
	
	private LocalDate returnedDate;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length=20)
	private BorrowStatus status;

}
