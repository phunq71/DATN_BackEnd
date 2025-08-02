package com.main.rest_controller;

import com.main.entity.Account;
import com.main.repository.AccountRepository;
import com.main.security.*;
import com.main.service.AccountService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class Admin_LoginRestController {

    @GetMapping("/admin/isLogin")
    public ResponseEntity<?> isAminLogin() {

        if(AuthUtil.isLogin()){
            System.err.println("🙂role đã đăng nhập từ FE "+AuthUtil.getRole());
            return ResponseEntity.ok(Map.of(
                    "loggedIn", true,
                    "displayName", Objects.requireNonNull(AuthUtil.getFullName())
            ));
        }
        return ResponseEntity.ok(Map.of("loggedIn", false));
    }


    @GetMapping("/admin/isAdmin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean isAdmin() {
        return true;
    }
}
