package com.startinpoint.lms.controller;

import com.startinpoint.lms.dto.request.UserCreateRequestDto;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@GetMapping("/login")
	public String showLoginPage(){
		return "book/login";
	}

	@GetMapping("/show-register")
	public String showRegisterPage(Model model){
		model.addAttribute("user",new User());
		return "book/register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("user") UserCreateRequestDto user, RedirectAttributes redirectAttributes){
		try{
			authService.registerUser(user);
			redirectAttributes.addFlashAttribute("successMessage","Account Created");
			return "redirect:/login";
		} catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
			return "redirect:/show-register";
        }
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response,Authentication authentication) {
		if(authentication !=null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}
		return "redirect:/user/home";
	}

}
