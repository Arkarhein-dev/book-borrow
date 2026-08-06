package com.startinpoint.lms.repository;

import java.util.List;

import com.startinpoint.lms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @EntityGraph(attributePaths = {"book"})
    Page<BorrowRecord> findByUserUsernameAndStatus(String username, BorrowStatus status,Pageable pageable);

    @EntityGraph(attributePaths = {"book"})
    Page<BorrowRecord> findByUserUsername(String username,Pageable pageable);

    boolean existsByBookIdAndUserUsernameAndStatus(Long bookId, String username, BorrowStatus status);

//    @Query("""
//    select br from BorrowRecord br join fetch br.user where br.book.id = :bookId and (:status is null or br.status = :status)
//    order by br.borrowDate desc
//""")
//    Page<BorrowRecord> fetchActiveBorrowedRecord(@Param("bookId")Long bookId, @Param("status")BorrowStatus status,Pageable pageable);

    @EntityGraph(attributePaths = {"user","book"})
    Page<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"user","book"})
    Page<BorrowRecord> findByUserId(Long userId,Pageable pageable);
}