package com.main.serviceImpl;

import com.main.entity.Account;
import com.main.repository.AccountRepository;
import com.main.repository.OrderRepository;
import com.main.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Autowired
    private OrderRepository orderRepository;

    private final AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Override
    public Boolean isOAuth2(String accountId) {
        Optional<Account> optionalAccount= accountRepository.findByAccountId(accountId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return account.getProvider() != null;
        }
        return false;
    }



    @Override
    public boolean checkPassword(String accountId, String password) {
        Account account= accountRepository.findByAccountId(accountId).get();
        return passwordEncoder.matches(password, account.getPassword());
    }

    @Override
    public boolean newPassword(String accountId, String password) {
        try {
            Account account = accountRepository.findByAccountId(accountId).get();
            account.setPassword(passwordEncoder.encode(password));
            accountRepository.save(account);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Optional<Account> findByEmailAndProviderIsNotNull(String email) {
        return accountRepository.findByEmailAndProviderIsNull(email);
    }

    //kiểm tra đơn hàng
    @Override
    public boolean hasPendingOrders(String accountId) {
        Optional<Account> accOpt = accountRepository.findById(accountId);
        if (accOpt.isEmpty() || accOpt.get().getCustomer() == null) {
            return false;
        }

        String customerId = accOpt.get().getCustomer().getCustomerId();

        List<String> pendingStatuses = List.of(
                "ChoXacNhan",
                "ChuanBiDon",
                "SanSangGiao",
                "DaYeuCauHuy",
                "ChoGiaoHang",
                "TraHang"
        );

        return orderRepository.existsByCustomer_CustomerIdAndStatusIn(customerId, pendingStatuses);
    }

    //vô hiệu hóa tk
    @Override
    public boolean deactivateAccount(String accountId) {
        Optional<Account> accOpt = accountRepository.findById(accountId);
        if (accOpt.isPresent()) {
            Account acc = accOpt.get();
            acc.setStatus(false); // Đặt trạng thái thành vô hiệu hóa
            acc.setUpdateAt(LocalDate.now());
            accountRepository.save(acc);
            return true;
        }
        return false;
    }

}
