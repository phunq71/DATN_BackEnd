package com.main.service;

import org.springframework.stereotype.Service;


public interface AccountService {
    public String generateAccountId();

    public Boolean isOAuth2(String accountId);

    public boolean checkPassword(String accountId, String password);

    public boolean newPassword(String accountId, String password);
}
