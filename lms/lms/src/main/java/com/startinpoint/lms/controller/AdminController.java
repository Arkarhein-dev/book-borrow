package com.startinpoint.lms.controller;

import com.startinpoint.lms.dto.request.BookCreateOrUpdateRequestDto;
import com.startinpoint.lms.dto.response.BookResponseDto;
import com.startinpoint.lms.dto.response.BorrowRecordResponseDto;
import com.startinpoint.lms.dto.response.UserResponseDto;
import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.service.BorrowRecordService;
import com.startinpoint.lms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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

	@GetMapping("/dashboard")
	public String adminDashboard(
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "6") int size,
			@RequestParam(name = "sortField", defaultValue = "title") String sortField,
			@RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
			Model model
	) {
		Page<BookResponseDto> bookPage;

		if (keyword != null && !keyword.trim().isEmpty()) {
			bookPage = bookService.getAllBooksWithKeyword(page, keyword.trim(), size, sortField, sortDir);
		} else {
			bookPage = bookService.getAllBooks(page, size, sortField, sortDir);
		}

		model.addAttribute("baseUrl", "/admin/dashboard");
		model.addAttribute("keyword", keyword);
		model.addAttribute("books", bookPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("totalPages", bookPage.getTotalPages());
		model.addAttribute("totalItems", bookPage.getTotalElements());
		model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");

		return "book/admin/dashboard";
	}

	@GetMapping("/books/new")
	public String showCreateForm(Model model) {
		model.addAttribute("book", new Book());
		return "book/admin/book-form";
	}
	
	@PostMapping("/books/save")
	public String saveBook(@Valid @ModelAttribute("book") BookCreateOrUpdateRequestDto dto) {
		bookService.saveOrUpdateBook(dto);
		return "redirect:/admin/dashboard";
	}
	
	@GetMapping("/books/edit/{id}")
	public String showEditForm(@PathVariable("id") Long id, Model model) {
		BookResponseDto existingBook = bookService.getBookById(id);
		model.addAttribute("book",existingBook);
		return "book/admin/book-form"; 
	}

	@PostMapping("/books/delete/{id}")
	public String deleteBook(@PathVariable("id") Long id) {
		bookService.deleteBook(id);
		return "redirect:/admin/dashboard";
	}


	@GetMapping("/user-lists")
	public String getAllUsers(
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "6") int pageSize,
			@RequestParam(name = "sortField", defaultValue = "username") String sortField,
			@RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
			Model model) {

		// Service call passing 'page' (1-indexed)
		Page<UserResponseDto> userPage = borrowRecordService.getAllUsers(page, pageSize, sortField, sortDir);

		model.addAttribute("users", userPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("totalPages", userPage.getTotalPages());
		model.addAttribute("totalItems", userPage.getTotalElements());
		model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");

		return "book/admin/user-lists";
	}


	@GetMapping("/users/{userId}/borrowed")
	public String fetchBorrowRecordsByUser(
			@PathVariable("userId") Long userId,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(name = "status", required = false) BorrowStatus status,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "6") int pageSize,
			@RequestParam(name = "sortField", defaultValue = "borrowDate") String sortField,
			@RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
			Model model
	){
		// Use UserResponseDto or map entity to DTO if getUser returns an entity
		UserResponseDto userDto = userService.getUser(userId);

		Page<BorrowRecordResponseDto> borrowRecordsPage;
		if (keyword != null && !keyword.trim().isEmpty()){
			borrowRecordsPage = borrowRecordService.fetchBorrowRecordByUserWithKeyword(
					userId, keyword.trim(), status, page, pageSize, sortField, sortDir
			);
		} else {
			borrowRecordsPage = borrowRecordService.fetchBorrowRecordByUser(
					userId, status, page, pageSize, sortField, sortDir
			);
		}

		model.addAttribute("baseUrl", "/admin/users/" + userId + "/borrowed");
		model.addAttribute("user", userDto);
		model.addAttribute("keyword", keyword);
		model.addAttribute("borrowRecords", borrowRecordsPage.getContent());
		model.addAttribute("selectedStatus", status);
		model.addAttribute("allStatuses", BorrowStatus.values());

		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("totalPages", borrowRecordsPage.getTotalPages());
		model.addAttribute("totalItems", borrowRecordsPage.getTotalElements());
		model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");

		return "book/admin/user-borrow-lists";
	}

}
