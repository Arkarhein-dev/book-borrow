package com.startinpoint.lms.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.startinpoint.lms.entity.Book;
import com.startinpoint.lms.service.BookService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	
	private final BookService bookService;
	
	
	
	@GetMapping("/home")
	public String userHome(Model model) {
		List<Book> books = bookService.getAllBooks();
		model.addAttribute("books",books);
		return "book/user/home"; 
	}
	
//	@GetMapping("/my-books")
//	public String getBorrowBooks(Model model) {
//		List<Book> borrowBooks 
//		model.addAttribute("borrowBooks",borrowBooks);
//		return "book/user/borrow-book";
//	}
	
	
	
	
	
	
}
