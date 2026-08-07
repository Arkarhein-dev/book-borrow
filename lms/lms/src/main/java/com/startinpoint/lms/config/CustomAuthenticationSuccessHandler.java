package com.startinpoint.lms.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String contextPath = request.getContextPath();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            // Checks if user has ADMIN authority
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect(contextPath+"/admin/dashboard");
                return;
            }
        }

        // Default redirect for regular USER
        response.sendRedirect(contextPath + "/user/home");
    }
}