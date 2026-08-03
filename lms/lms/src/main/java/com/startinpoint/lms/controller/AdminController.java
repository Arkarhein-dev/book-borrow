package com.startinpoint.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.startinpoint.lms.entity.Book;
import com.startinpoint.lms.service.BookService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	private final BookService bookService;
	
	@GetMapping("/dashboard")
	public String adminDashboard(Model model) {
		model.addAttribute("books", bookService.getAllBooks());
		return "book/admin/dashboard";
	}
	
	
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
	
}
