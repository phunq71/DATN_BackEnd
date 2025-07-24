package com.main.rest_controller;

import com.main.entity.Account;
import com.main.repository.AccountRepository;
import com.main.security.*;
import com.main.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Admin_LoginRestController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;


    @PostMapping("/auth/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) {
        System.err.println("⭐⭐ được gọi rồi nè");


        // 1. Xác thực user (check email + password với DB)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Load lại UserDetails để lấy dữ liệu user
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(request.getEmail());

        // 3. Sinh Access Token
        String accessToken = jwtService.generateToken(userDetails, 1000 * 60 * 60); // VD: 1 giờ

        // 4. Sinh Refresh Token
        String refreshToken = jwtService.generateToken(userDetails, 1000 * 60 * 60 * 24 * 7); // VD: 7 ngày


        // 5. Trả về FE
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }
}
