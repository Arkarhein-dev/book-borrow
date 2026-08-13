package com.startinpoint.lms.repository;

import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    // --- Derived Query Methods ---

    @EntityGraph(attributePaths = {"book"})
    Page<BorrowRecord> findByUserUsernameAndStatus(String username, BorrowStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"book"})
    Page<BorrowRecord> findByUserUsername(String username, Pageable pageable);

    boolean existsByBookIdAndUserUsernameAndStatus(Long bookId, String username, BorrowStatus status);

    @EntityGraph(attributePaths = {"user", "book"})
    Page<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "book"})
    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);


    // --- Search Queries with Keyword & Optional Null Safety ---
    @EntityGraph(attributePaths = {"user", "book"})
    @Query("""
        SELECT br FROM BorrowRecord br
        WHERE br.user.username = :username
          AND (
            :keyword IS NULL OR :keyword = '' OR
            LOWER(br.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(br.book.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<BorrowRecord> findBorrowBookBykeyword(
            @Param("username") String username,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user", "book"})
    @Query("""
        SELECT br FROM BorrowRecord br
        WHERE br.user.username = :username
          AND br.status = :status
          AND (
            :keyword IS NULL OR :keyword = '' OR
            LOWER(br.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(br.book.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<BorrowRecord> findBorrowRecordByKeywordAndStatus(
            @Param("username") String username,
            @Param("keyword") String keyword,
            @Param("status") BorrowStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user", "book"})
    @Query("""
    SELECT br FROM BorrowRecord br
    WHERE br.user.id = :userId
      AND (
        :keyword IS NULL OR :keyword = '' OR
        LOWER(br.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(br.book.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
""")
    Page<BorrowRecord> fetchBorrowRecordByUserWithKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user", "book"})
    @Query("""
    SELECT br FROM BorrowRecord br
    WHERE br.user.id = :userId
      AND br.status = :status
      AND (
        :keyword IS NULL OR :keyword = '' OR
        LOWER(br.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(br.book.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
""")
    Page<BorrowRecord> fetchBorrowRecordByUserWithKeywordAndStatus(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("status") BorrowStatus status,
            Pageable pageable
    );

    // find borrow records that have been overdued to return
    @Query("""
    select br from BorrowRecord br where br.returnedDate is null and br.dueDate < :today
""")
    List<BorrowRecord> findOverdueUnreturnedBooks(@Param("today")LocalDate today);

    @Transactional
    @Modifying
    @Query("""
    update BorrowRecord  br
    set br.status = com.startinpoint.lms.entity.BorrowStatus.OVERDUE
    where br.status = com.startinpoint.lms.entity.BorrowStatus.BORROWED
    and br.returnedDate is null
    and br.dueDate < CURRENT_DATE
""")
    int updateOverdueStatusesOnStartUp();
}