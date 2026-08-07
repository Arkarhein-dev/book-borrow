package com.startinpoint.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.startinpoint.lms.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Page<Book> findByAvailableTrue(Pageable pageable);

    Page<Book> findByAvailableTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("""
        select b from Book b where lower(b.title) like lower(concat('%',:keyword, '%')) or 
        lower(b.author) like lower(concat('%',:keyword, '%') ) 
""")
    Page<Book> searchBook(@Param("keyword") String keyword, Pageable pageable);
}
