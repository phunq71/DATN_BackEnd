package com.main.security;

import com.main.entity.Account;
import com.main.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         Account account = accRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(account.getAccountId())
                .password(account.getPassword())
                .roles(account.getRole())
                .disabled(!account.getStatus())
                .build();
    }
}

