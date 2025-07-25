package com.main.mapper;

import com.main.dto.InFoCustomerOrderDTO;
import com.main.entity.Customer;
import com.main.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerMapper {
    private final CustomerRepository customerRepository;

    public InFoCustomerOrderDTO toInFoCustomerOrderDTO(Customer customer) {
        InFoCustomerOrderDTO dto = new InFoCustomerOrderDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerName(customer.getFullName());
        dto.setCustomerAddress(customer.getAddress());
        dto.setCustomerAddressIdGHN(customer.getAddressIdGHN());
        dto.setCustomerPhone(customer.getPhone());
        dto.setCustomerAddressIdGHN(customer.getAddressIdGHN());
        dto.setNote(null);
        return dto;
    }
}
