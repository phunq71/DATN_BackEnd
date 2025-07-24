package com.main.rest_controller;

import com.main.security.*;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            authService.authenticate(request);
            UserDetails userDetails = authService.loadUser(request.getEmail());

            Map<String, ResponseCookie> cookies = authService.generateTokenCookies(userDetails, request.isRememberMe());


            response.addHeader("Set-Cookie", cookies.get("accessToken").toString());
            response.addHeader("Set-Cookie", cookies.get("refreshToken").toString());

            return ResponseEntity.ok("Đăng nhập thành công");

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
        }
    }

    @PostMapping("/login2")
    public ResponseEntity<?> login2(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(request.getEmail());

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);



            Map<String, ResponseCookie> cookies = authService.generateTokenCookies(userDetails, request.isRememberMe());


            response.addHeader("Set-Cookie", cookies.get("accessToken").toString());
            response.addHeader("Set-Cookie", cookies.get("refreshToken").toString());

            String role = AuthUtil.getRole();
            if(role== null || role.equals("ROLE_USER")){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            return ResponseEntity.ok("Đăng nhập thành công");

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestBody Map<String, Object> body,
            HttpServletResponse response) {

        boolean rememberMe = Boolean.TRUE.equals(body.get("rememberMe"));

        if (!StringUtils.hasText(refreshToken) || !authService.isValidToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token không hợp lệ");
        }

        String email = jwtService.getEmailFromToken(refreshToken);
        UserDetails userDetails = authService.loadUser(email);

        Map<String, ResponseCookie> cookies = authService.generateTokenCookies(userDetails, rememberMe);

        response.addHeader("Set-Cookie", cookies.get("accessToken").toString());
        response.addHeader("Set-Cookie", cookies.get("refreshToken").toString());

        return ResponseEntity.ok("Refresh thành công");
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok("Đăng xuất thành công");
    }

}


