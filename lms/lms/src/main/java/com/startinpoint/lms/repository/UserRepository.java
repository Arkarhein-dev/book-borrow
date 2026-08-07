package com.startinpoint.lms.repository;

import com.startinpoint.lms.entity.User;

import java.util.List;
import java.util.Optional;

import com.startinpoint.lms.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	Optional<User> findById(Long id);

	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	boolean existsByRole(UserRole role);

}
