package com.startinpoint.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.startinpoint.lms.entity.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Page<Book> findByAvailableTrue(Pageable pageable);

    Page<Book> findByAvailableTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);

    // 1. Available books without keyword
    Page<Book> findByStockGreaterThan(int stock, Pageable pageable);

    // 2. Available books WITH keyword
    @Query("""
        SELECT b FROM Book b
        WHERE b.stock > 0 
        AND (
            LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<Book> searchAvailableBooksWithKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 3. All books WITH keyword
    @Query("""
        SELECT b FROM Book b WHERE 
        LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
        LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Book> searchBookWithKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<Book> findByStockEquals(int stock);
}
