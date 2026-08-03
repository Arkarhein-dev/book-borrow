package com.startinpoint.lms.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status);

    List<BorrowRecord> findByUserUsername(String username);

    boolean existsByBookIdAndUserUsernameAndStatus(Long bookId, String username, BorrowStatus status);
}