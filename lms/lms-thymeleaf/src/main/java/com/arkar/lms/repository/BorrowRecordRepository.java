package com.arkar.lms.repository;

import com.arkar.lms.entity.BorrowRecord;
import com.arkar.lms.entity.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord,Long> {

    boolean existsByBookIdAndUserUsernameAndStatus(Long bookId,String username,BorrowStatus status);
}
