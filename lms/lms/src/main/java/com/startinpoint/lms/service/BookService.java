package com.startinpoint.lms.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.startinpoint.lms.entity.Book;
import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.repository.BookRepository;
import com.startinpoint.lms.repository.BorrowRecordRepository;
import com.startinpoint.lms.repository.UserRepository;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
	
	private final BookRepository bookRepository;
	private final UserRepository userRepository;
	private final BorrowRecordRepository borrowRecordRepository;
	private final EmailService emailService;

	public Page<Book> getAllBooks(int pageNo, int pageSize, String sortField, String sortDir){

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
				? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();
		Pageable pageable= PageRequest.of(pageNo-1,pageSize,sort);
		return bookRepository.findAll(pageable);
	}

	// Search Books with By keyword
	public Page<Book> searchBook(String keyword,int pageNo, int pageSize, String sortField, String sortDir ){
		int pageIndex = (pageNo < 1) ? 0 : pageNo - 1;
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
				? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
		return bookRepository.searchBook(keyword, pageable);
	}

	public Book getBookById(long id) {
		return bookRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("Book Not Found."));
	}

	public Book saveOrUpdateBook(Book book) {
		if(book.isAvailable() || book.getStock() >0){
			book.setAvailable(true);
		}
		return bookRepository.save(book);
	}
	
	public void deleteBook(Long id) {
		bookRepository.deleteById(id);
	}



	
	@Transactional
	public void borrowBook(Long bookId, String username) {
		Book book = getBookById(bookId);
		
		if(!book.isAvailable() || book.getStock() <=0) {
			emailService.sendOutOfStockNotificationToAdmin(book.getTitle(),book.getId(),username);
			throw new IllegalArgumentException("Sry, this book is out of stock.");
		}
		
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User Not Found with name "+username));
		
		boolean isBorrowed = borrowRecordRepository.existsByBookIdAndUserUsernameAndStatus(bookId, username, BorrowStatus.BORROWED);
		
		if(isBorrowed) {
			throw new IllegalArgumentException("Book is already borrowed...");
		}
		
		BorrowRecord borrowRecord = new BorrowRecord();
		borrowRecord.setBook(book);
		borrowRecord.setUser(user);
		borrowRecord.setBorrowDate(LocalDate.now());
		borrowRecord.setDueDate(LocalDate.now().plusDays(14));
		borrowRecord.setStatus(BorrowStatus.BORROWED);
		borrowRecordRepository.save(borrowRecord);
		
		book.setStock(book.getStock()-1);
		if(book.getStock()<=0) {
			book.setAvailable(false);
		}
		bookRepository.save(book);
	}
	
	@Transactional
	public void returnBook(Long recordId) {
		BorrowRecord record = borrowRecordRepository.findById(recordId)
				.orElseThrow(() -> new IllegalArgumentException("Record Not Found with id "+recordId));
		
		if(record.getStatus()==BorrowStatus.RETURNED) {
			throw new IllegalArgumentException("Book is already Returned...");
		}

		record.setReturnedDate(LocalDate.now());
		record.setStatus(BorrowStatus.RETURNED);
		borrowRecordRepository.save(record);

		Book book = record.getBook();
		book.setStock(book.getStock()+1);
		book.setAvailable(true);
		bookRepository.save(book);
	}

}
