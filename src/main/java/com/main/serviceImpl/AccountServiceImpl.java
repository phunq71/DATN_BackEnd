package com.main.serviceImpl;

import com.main.entity.Account;
import com.main.repository.AccountRepository;
import com.main.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public String generateAccountId() {
        String maxId = accountRepository.findMaxAccountId(); // VD: "ACC000000023"
        long nextId = 1;

        if (maxId != null && maxId.startsWith("ACC")) {
            try {
                long current = Long.parseLong(maxId.substring(3));
                nextId = current + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("AccountID không hợp lệ: " + maxId);
            }
        }

        return String.format("ACC%09d", nextId); // VD: ACC000000024
    }
}
