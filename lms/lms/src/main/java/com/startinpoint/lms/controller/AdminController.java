package com.startinpoint.lms.controller;

import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.service.BorrowRecordService;
import com.startinpoint.lms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.startinpoint.lms.entity.Book;
import com.startinpoint.lms.service.BookService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	private final BookService bookService;
	private final BorrowRecordService borrowRecordService;
	private final UserService userService;
	
//	@GetMapping("/dashboard")
//	public String adminDashboard(Model model) {
//		model.addAttribute("books", bookService.getAllBooks());
//		return "book/admin/dashboard";
//	}

	@GetMapping("/books/new")
	public String showCreateForm(Model model) {
		model.addAttribute("book", new Book());
		return "book/admin/book-form";
	}
	
	@PostMapping("/books/save")
	public String saveBook(@ModelAttribute("book") Book book) {
		bookService.saveOrUpdateBook(book);
		return "redirect:/admin/dashboard";
	}
	
	@GetMapping("/books/edit/{id}")
	public String showEditForm(@PathVariable("id") Long id, Model model) {
		Book existingBook = bookService.getBookById(id);
		model.addAttribute("book",existingBook);
		return "book/admin/book-form"; 
	}

	@PostMapping("/books/delete/{id}")
	public String deleteBook(@PathVariable("id") Long id) {
		bookService.deleteBook(id);
		return "redirect:/admin/dashboard"; 
	}

	@GetMapping("/{bookId}/borrowers")
	public String viewBorrowRecords(
			@PathVariable("bookId")Long bookId,
			@RequestParam(value = "status",required = false)BorrowStatus status,
			Model model){

		List<BorrowRecord> borrowRecords = borrowRecordService.getBorrowRecordsByBookAndStatus(bookId,status);
		Book book = bookService.getBookById(bookId);

		model.addAttribute("book",book);
		model.addAttribute("borrowRecords",borrowRecords);
		model.addAttribute("selectedStatus",status);
		model.addAttribute("allStatuses",BorrowStatus.values());

		return "book/admin/book-borrowers";
	}

	@GetMapping("/user-lists")
	public String getAllUsers(Model model){
		model.addAttribute("users",borrowRecordService.getAllUsers());
		return "book/admin/user-lists";
	}

	@GetMapping("/users/{userId}/borrowed")
	public String fetchBorrowRecordsByUser(
			@PathVariable("userId")Long userId,
			@RequestParam(name = "status", required = false) BorrowStatus status,
			Model model
			){
		User user = userService.getUser(userId);
		List<BorrowRecord> borrowRecords = borrowRecordService.fetchBorrowRecordByUser(userId, status);
		model.addAttribute("user",user);
		model.addAttribute("borrowRecords",borrowRecords);
		model.addAttribute("selectedStatus",status);
		model.addAttribute("allStatuses",BorrowStatus.values());

		return "book/admin/user-borrow-lists";
	}
}
