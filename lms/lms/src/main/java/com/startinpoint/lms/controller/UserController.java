package com.startinpoint.lms.controller;

import java.util.List;

import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import com.startinpoint.lms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
	public String userHome(
			@RequestParam(value = "page",defaultValue = "1")int page,
			@RequestParam(value = "size",defaultValue = "6")int size,
			@RequestParam(value = "sortField",defaultValue = "title")String sortField,
			@RequestParam(value = "sortDir",defaultValue = "asc")String sortDir,
			Model model
	){
		Page<Book> bookPage = bookService.getAllBooks(page,size,sortField,sortDir);
		model.addAttribute("books",bookPage.getContent());
		model.addAttribute("currentPage",page);
		model.addAttribute("pageSize",size);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("totalPages",bookPage.getTotalPages());
		model.addAttribute("totalItems",bookPage.getTotalElements());
		model.addAttribute("reverseSortDir",sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");
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
