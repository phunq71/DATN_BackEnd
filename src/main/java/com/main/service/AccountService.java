package com.main.service;

import com.main.entity.Account;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface AccountService {
    public String generateAccountId();

    public Boolean isOAuth2(String accountId);

    public boolean checkPassword(String accountId, String password);

    public boolean newPassword(String accountId, String password);

    public Optional<Account> findByEmailAndProviderIsNotNull(String email);

    //kiểm tra đơn hàng
    boolean hasPendingOrders(String accountId);

    // vô hiệu hóa tk
    boolean deactivateAccount(String accountId);
    Optional<Account> finByAccountId(String accountId);
}
