package com.main.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class OAuth2RefererSavingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        HttpSession session = request.getSession();

        // Chỉ lưu nếu chưa có redirect_uri và đang chuẩn bị đi login
        if ((uri.equals("/auth") || uri.startsWith("/oauth2/authorization"))
                && session.getAttribute("redirect_uri") == null) {

            String referer = request.getHeader("Referer");

            if (referer != null) {
                // Chuyển "index2" → "index" nếu trùng khớp chính xác
                if (referer.endsWith("/index2") || referer.equals("index2")) {
                    referer = referer.replace("index2", "index");
                }

                // Tránh lưu nếu referer là chính trang /auth
                if (!referer.contains("/auth")) {
                    session.setAttribute("redirect_uri", referer);
                }
            }
        }


        filterChain.doFilter(request, response);
    }
}


