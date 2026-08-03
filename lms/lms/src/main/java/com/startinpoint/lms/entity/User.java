package com.startinpoint.lms.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 150)
	private String username;
	
	@Column(nullable = false, length = 50)
	private String password;
	
	@Column(nullable = false)
	private String role;
	
	@Column(nullable = false)
	private boolean isActive;
	
	@OneToMany(mappedBy = "user")
	private List<BorrowRecord> borrowRecords;
}
