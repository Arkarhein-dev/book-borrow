package com.startinpoint.lms.controller;


import com.startinpoint.lms.dto.response.BookResponseDto;
import com.startinpoint.lms.dto.response.BorrowRecordResponseDto;
import com.startinpoint.lms.entity.BorrowStatus;
import com.startinpoint.lms.service.BorrowRecordService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.startinpoint.lms.service.BookService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	
	private final BookService bookService;
	private final BorrowRecordService borrowRecordService;

	@GetMapping("/home")
	public String userHome(
			@RequestParam(value = "availableOnly", required = false) Boolean availableOnly,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "6") int size,
			@RequestParam(value = "sortField", defaultValue = "title") String sortField,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
			Model model
	){
		Page<BookResponseDto> bookPage;
		Boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
		Boolean isAvailableOnly = Boolean.TRUE.equals(availableOnly);

		if(hasKeyword && isAvailableOnly){
			bookPage = bookService.getAvailableBooksWithKeyword(keyword.trim(), page, size, sortField, sortDir);
		} else if(isAvailableOnly){
			bookPage = bookService.getAvailableBooks(page, size, sortField, sortDir);
		} else if(hasKeyword){
			bookPage = bookService.searchBook(keyword.trim(), page, size, sortField, sortDir);
		} else {
			bookPage = bookService.getAllBooks(page, size, sortField, sortDir);
		}

		// Guard against out-of-bounds page requests after toggling filters
		if (page > bookPage.getTotalPages() && bookPage.getTotalPages() > 0) {
			StringBuilder redirectUrl = new StringBuilder(
					String.format("redirect:/user/home?page=%d&size=%d&sortField=%s&sortDir=%s",
							bookPage.getTotalPages(), size, sortField, sortDir)
			);
			if (hasKeyword) redirectUrl.append("&keyword=").append(keyword.trim());
			if (availableOnly != null) redirectUrl.append("&availableOnly=").append(availableOnly);
			return redirectUrl.toString();
		}

		model.addAttribute("baseUrl", "/user/home"); // Required by pagination fragment
		model.addAttribute("books", bookPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("totalPages", bookPage.getTotalPages());
		model.addAttribute("totalItems", bookPage.getTotalElements());
		model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");

		model.addAttribute("keyword", keyword);
		model.addAttribute("availableOnly", availableOnly);

		return "book/user/home";
	}


	@GetMapping("/my-books")
	public String getBorrowBooks(
			Authentication authentication,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "status", required = false) BorrowStatus status,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "6") int size,
			@RequestParam(value = "sortField", defaultValue = "borrowDate") String sortField,
			@RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
			Model model
	) {
		Page<BorrowRecordResponseDto> pageResult;
		String username = authentication.getName();

		if (keyword != null && !keyword.trim().isEmpty()) {
			pageResult = borrowRecordService.fetchBorrowRecordByKeyword(
					keyword.trim(), username, status, page, size, sortField, sortDir
			);
		} else {
			pageResult = borrowRecordService.getUserActiveBorrowRecords(
					username, status, page, size, sortField, sortDir
			);
		}

		model.addAttribute("baseUrl", "/user/my-books");
		model.addAttribute("keyword", keyword);
		model.addAttribute("borrowRecords", pageResult.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pageResult.getTotalPages());
		model.addAttribute("totalItems", pageResult.getTotalElements());
		model.addAttribute("pageSize", size);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("selectedStatus", status);
		model.addAttribute("allStatuses", BorrowStatus.values());

		return "book/user/my-books";
	}

	@PostMapping("/borrow-book/{id}")
	public String borrowBook(
			@PathVariable("id") Long bookId,
			@RequestParam(value = "dueDate", defaultValue = "14") Integer dueDate,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "6") int size,
			@RequestParam(value = "sortField", defaultValue = "title") String sortField,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "availableOnly", required = false) Boolean availableOnly,
			Authentication authentication,
			RedirectAttributes redirectAttributes) {

		try {
			int days = (dueDate == null || dueDate < 1 || dueDate > 14) ? 14 : dueDate;
			String username = authentication.getName();

			bookService.borrowBook(bookId, username, days);

			redirectAttributes.addFlashAttribute("successMessage",
					String.format("Book borrowed successfully for %d day(s)!", days));

		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while borrowing the book.");
		}

		// ALWAYS REDIRECT BACK TO /user/home PRESERVING QUERY PARAMS
		StringBuilder redirectUrl = new StringBuilder(
				String.format("redirect:/user/home?page=%d&size=%d&sortField=%s&sortDir=%s", page, size, sortField, sortDir)
		);

		if (keyword != null && !keyword.trim().isEmpty()) {
			redirectUrl.append("&keyword=").append(keyword);
		}
		if (availableOnly != null) {
			redirectUrl.append("&availableOnly=").append(availableOnly);
		}

		return redirectUrl.toString(); // e.g. redirect:/user/home?page=1&size=6&sortField=title&sortDir=asc
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
