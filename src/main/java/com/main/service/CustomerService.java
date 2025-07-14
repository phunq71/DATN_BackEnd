package com.main.service;

import com.main.dto.CustomerDTO;
import com.main.entity.Customer;
import com.main.dto.CustomerRegisterDTO;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    public String generateCustomerId();

    public CustomerDTO getCustomerDTOByaccountId(String accountId);

    public CustomerDTO saveCustomer(CustomerDTO customerDTO, MultipartFile file);

    public Customer save(Customer customer);

    public Customer findByAccountID(String accountId);

    public boolean saveCustomerRegister(CustomerRegisterDTO customerRegisterDTO);
}
