package com.startinpoint.lms.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
	
	@ManyToOne(optional = false)
	private User user;
	
	@ManyToOne(optional = false)
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
