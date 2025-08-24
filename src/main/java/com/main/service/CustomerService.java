package com.main.service;

import com.main.dto.CustomerDTO;
import com.main.dto.CustomerManagementDTO;
import com.main.dto.ReviewStatsDTO;
import com.main.entity.Customer;
import com.main.dto.CustomerRegisterDTO;
import com.main.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CustomerService {
    public String generateCustomerId();

    public CustomerDTO getCustomerDTOByaccountId(String accountId);

    public CustomerDTO saveCustomer(CustomerDTO customerDTO, MultipartFile file);

    public Customer save(Customer customer);

    public Customer findByAccountID(String accountId);

    public boolean saveCustomerRegister(CustomerRegisterDTO customerRegisterDTO);

    public byte[] createQRCode();

    public Customer verifyQRCodeToken(String qrToken);

    public Boolean updateAddrBoolean(String Address, String AddressIdGHN);


    public Void updateRankByCustomerId(Customer customer);

    Page<CustomerManagementDTO> getAllCustomer(String membershipId, String customerId , int page);

    Boolean deleteCustomer(String customerId);

    Map<String, Object> findAllAddresses();


}
