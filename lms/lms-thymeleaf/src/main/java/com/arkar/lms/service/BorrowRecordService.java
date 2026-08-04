package com.arkar.lms.service;

import com.arkar.lms.entity.Book;
import com.arkar.lms.entity.BorrowRecord;
import com.arkar.lms.entity.BorrowStatus;
import com.arkar.lms.entity.User;
import com.arkar.lms.repository.BookRepository;
import com.arkar.lms.repository.BorrowRecordRepository;
import com.arkar.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // Helper Method
    public Book getBookById(Long bookId){
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book Not Found..."));
    }

    @Transactional
    public void borrowBook(Long bookId, String username){
        Book borrowBook = getBookById(bookId);
        User borrowUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found..."));

        if(borrowBook.getStock() <= 0 || !borrowBook.isAvailable()){
            throw  new RuntimeException("Book is out of Stock");
        }

        boolean isBorrowed = borrowRecordRepository.existsByBookIdAndUserUsernameAndStatus(bookId,username, BorrowStatus.BORROWED);
        if(isBorrowed){
            throw new IllegalArgumentException("Book is already borrowed...");
        }
        BorrowRecord record = new BorrowRecord();
        record.setBook(borrowBook);
        record.setUser(borrowUser);
        record.setStatus(BorrowStatus.BORROWED);
        record.setBorrowedDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        borrowRecordRepository.save(record);

        borrowBook.setStock(borrowBook.getStock()-1);
        if(borrowBook.getStock() < 0){
            borrowBook.setAvailable(false);
        }
        bookRepository.save(borrowBook);
    }

    @Transactional
    public void returnBook(Long recordId){
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record Not found..."));
        if(record.getStatus() == BorrowStatus.RETURNED){
            throw new IllegalArgumentException("Book is already returned...");
        }

        record.setReturnedDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);
        borrowRecordRepository.save(record);

        Book returnBook = record.getBook();
        returnBook.setStock(returnBook.getStock()+1);
        returnBook.setAvailable(true);
        bookRepository.save(returnBook);
    }
}
