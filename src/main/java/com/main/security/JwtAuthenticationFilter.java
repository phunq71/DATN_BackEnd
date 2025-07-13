package com.main.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;


    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        System.out.println("➡️ Token: " + token);
        System.out.println("📥 Incoming URI: " + request.getRequestURI());

        boolean validAccessToken = (token != null && jwtService.validateToken(token));

        if (!validAccessToken) {
            // ➕ Thử dùng refresh token nếu access token hết hạn
            String refreshToken = getRefreshTokenFromRequest(request);
            if (refreshToken != null && jwtService.validateToken(refreshToken)) {
                System.out.println("🔁 Access token hết hạn, dùng refresh token để cấp mới");

                String provider = jwtService.getProviderFromToken(refreshToken);
                String providerId = jwtService.getProviderIdFromToken(refreshToken);
                String email = jwtService.getEmailFromToken(refreshToken); // Nếu cần

                UserDetails userDetails;
                if (provider == null) {
                    userDetails = userDetailsService.loadUserByUsername(email);
                } else {
                    userDetails = userDetailsService.loadUserByProvider(provider, providerId);
                }

                int accessTokenMinutes = 15;

// 🔐 Xác thực người dùng
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

// 🔄 Ép kiểu về CustomUserDetails và cấp lại access token
                CustomUserDetails customUser = (CustomUserDetails) userDetails;
                String newAccessToken = jwtService.generateToken(customUser, accessTokenMinutes);

                Cookie accessCookie = new Cookie("accessToken", newAccessToken);
                accessCookie.setPath("/");
                accessCookie.setHttpOnly(true);
                accessCookie.setMaxAge(accessTokenMinutes * 60); // phút → giây
                response.addCookie(accessCookie);


            } else {
                System.out.println("⚠️ Access token hết hạn và refresh token không hợp lệ");
            }
        } else {
            // ✅ Token hợp lệ → xác thực
            try {
                String provider = jwtService.getProviderFromToken(token);
                String providerId = jwtService.getProviderIdFromToken(token);
                UserDetails userDetails;

                if (provider == null) {
                    String email = jwtService.getEmailFromToken(token);
                    userDetails = userDetailsService.loadUserByUsername(email);
                } else {
                    userDetails = userDetailsService.loadUserByProvider(provider, providerId);
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (UsernameNotFoundException e) {
                System.out.println("❌ Không tìm thấy tài khoản đăng nhập");
            }
        }

        filterChain.doFilter(request, response);
    }



    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Optional: hỗ trợ trong header nếu cần
        String refreshHeader = request.getHeader("Refresh-Token");
        if (refreshHeader != null && refreshHeader.startsWith("Bearer ")) {
            return refreshHeader.substring(7);
        }

        return null;
    }


}

