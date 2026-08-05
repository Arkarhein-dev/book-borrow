package com.startinpoint.lms.service;

import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.repository.BookRepository;
import com.startinpoint.lms.repository.BorrowRecordRepository;
import com.startinpoint.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;

    // fetchBorrowRecord By BookId and Status
    public List<BorrowRecord> getBorrowRecordsByBookAndStatus(Long bookId, BorrowStatus status){
        return borrowRecordRepository.fetchActiveBorrowedRecord(bookId, status);
    }

    // Get all Users
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    // fetch borrow Record by User
    public List<BorrowRecord> fetchBorrowRecordByUser(Long userId, BorrowStatus status){
        if(status != null){
            return borrowRecordRepository.fetchBorrowRecordByUserIdAndStatus(userId,status);
        }
        return borrowRecordRepository.fetchBorrowRecordByUserId(userId);
    }
}
