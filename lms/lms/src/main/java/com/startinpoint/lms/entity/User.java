package com.startinpoint.lms.entity;

import java.util.List;

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
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 150)
	private String username;

	@Column(nullable = false, length = 150, unique = true)
	private String email;
	
	@Column(nullable = false, length = 150)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;
	
	@Column(nullable = false)
	private boolean isActive;
	
	@OneToMany(mappedBy = "user")
	private List<BorrowRecord> borrowRecords;

	public User(String username, String email, String password, UserRole role, boolean isActive) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.isActive = isActive;
	}
}
