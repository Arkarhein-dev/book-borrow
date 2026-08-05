package com.startinpoint.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.startinpoint.lms.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Page<Book> findByAvailableTrue(Pageable pageable);

    Page<Book> findByAvailableTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);
}
