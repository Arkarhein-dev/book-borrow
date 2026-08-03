package com.startinpoint.lms.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 150, nullable = false)
	private String title;
	
	@Column(length = 150, nullable = false)
	private String author;
	
	@Column(nullable = false)
	private int stock;
	
	@Column(nullable = false)
	private boolean isAvailable;
	
	@OneToMany(mappedBy = "book")
	private List<BorrowRecord> borrowRecords;
}
