package com.startinpoint.lms.controller;

import java.util.List;

import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import com.startinpoint.lms.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.startinpoint.lms.entity.Book;
import com.startinpoint.lms.service.BookService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	
	@GetMapping("/my-books")
	public String getBorrowBooks(Authentication authentication, Model model) {
		String username = authentication.getName();
		List<BorrowRecord> records = bookService.getUserActiveBorrowRecords(username, BorrowStatus.BORROWED);
		model.addAttribute("records",records);
		return "book/user/my-books";
	}

	@PostMapping("/borrow-book/{id}")
	public String borrowBook(@PathVariable("id")Long bookId,
							 Authentication authentication,
							 RedirectAttributes redirectAttributes){
		try{
			String username = authentication.getName();
			bookService.borrowBook(bookId,username);
			redirectAttributes.addFlashAttribute("successMessage","Book borrowed Successfully..");
		}catch (Exception e){
			redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
		}
		return "redirect:/user/home";
	}

	@PostMapping("/return-book/{recordId}")
	public String returnBook(
			@PathVariable("recordId")Long recordId,
			RedirectAttributes redirectAttributes
	){
		try{
			bookService.returnBook(recordId);
			redirectAttributes.addFlashAttribute("successMessage","Book returned Successfully...");
		}catch (Exception e){
			redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
		}
		return "redirect:/user/my-books";
	}
	
	
	
	
	
	
}
