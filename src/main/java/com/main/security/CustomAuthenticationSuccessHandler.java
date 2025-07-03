package com.main.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            // Ưu tiên redirect lại trang gốc nếu có (áp dụng cho cả USER và ADMIN)
            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
            if (savedRequest != null) {
                response.sendRedirect(savedRequest.getRedirectUrl());
                return;
            }

            String redirectUri = (String) request.getSession().getAttribute("redirect_uri");
            if (redirectUri != null) {
                response.sendRedirect(redirectUri);
                request.getSession().removeAttribute("redirect_uri"); // clear sau khi dùng
                return;
            }

            // Nếu không có URL trước đó, phân quyền theo vai trò
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();

                if (role.equals("ROLE_ADMIN")) {
                    response.sendRedirect("http://localhost:5173/home");
                    return;
                } else if (role.equals("ROLE_USER")) {
                    response.sendRedirect("/index?login");
                    return;
                }
            }

        } catch (Exception ex) {
            logger.error("❌ Lỗi trong onAuthenticationSuccess: {}", ex.getMessage(), ex);
            response.sendRedirect("/auth?error=server_error");
            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT 👅");// Có thể hiển thị thông báo ở trang auth
        }
    }
}

