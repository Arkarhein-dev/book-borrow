package com.startinpoint.lms.repository;

import java.util.List;

import com.startinpoint.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUserUsernameAndStatus(String username, BorrowStatus status);

    List<BorrowRecord> findByUserUsername(String username);

    boolean existsByBookIdAndUserUsernameAndStatus(Long bookId, String username, BorrowStatus status);


    @Query("""
    select br from BorrowRecord br join fetch br.user where br.book.id = :bookId and (:status is null or br.status = :status)
    order by br.borrowDate desc 
""")
    List<BorrowRecord> fetchActiveBorrowedRecord(@Param("bookId")Long bookId, @Param("status")BorrowStatus status);


    @Query("""
    select br from BorrowRecord br join fetch br.user 
    where br.user.id = :userId and (:status is null or br.status = :status)
    order by br.borrowDate desc
""")
    List<BorrowRecord> fetchBorrowRecordByUserIdAndStatus(@Param("userId") Long userId,@Param("status") BorrowStatus status);

    @Query("""
    select br from BorrowRecord br join fetch br.user 
    where br.user.id = :userId
    order by br.borrowDate desc
""")
    List<BorrowRecord> fetchBorrowRecordByUserId(@Param("userId")Long userId);
}