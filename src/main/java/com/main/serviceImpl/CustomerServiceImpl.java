package com.main.serviceImpl;

import com.main.repository.CustomerRepository;
import com.main.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public String generateCustomerId() {
        String maxId = customerRepository.findMaxCustomerId(); // VD: CUS000000123

        long nextId = 1;
        if (maxId != null && maxId.startsWith("CUS")) {
            try {
                // Tách phần số, ví dụ: từ "CUS000000123" → "000000123" → 123
                long current = Long.parseLong(maxId.substring(3));
                nextId = current + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("CustomerID không hợp lệ: " + maxId);
            }
        }

        return String.format("CUS%09d", nextId); // Kết quả: "CUS000000124"
    }

}

