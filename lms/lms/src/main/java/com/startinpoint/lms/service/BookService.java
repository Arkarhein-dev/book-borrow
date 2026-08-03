package com.startinpoint.lms.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.startinpoint.lms.entity.Book;
import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.repository.BookRepository;
import com.startinpoint.lms.repository.UserRepository;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
	
	private final BookRepository bookRepository;
	private final UserRepository userRepository;
	private final BorrowRecord borrowRecord;
	
	public List<Book> getAllBooks(){
		return bookRepository.findAll();
	}
	
	public Book getBookById(long id) {
		return bookRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("Book Not Found."));
	}

	public Book saveOrUpdateBook(Book book) {
		return bookRepository.save(book);
	}
	
	public void deleteBook(Long id) {
		bookRepository.deleteById(id);
	}
	
	public void borrowBook(Long bookId, String username) {
		Book book = getBookById(bookId);
		
		User user = userRepository.findByUsername(username).orElseThrow();
	}
	
	
}
