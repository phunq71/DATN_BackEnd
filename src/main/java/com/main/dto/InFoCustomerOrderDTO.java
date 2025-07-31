package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InFoCustomerOrderDTO {
    private String customerId;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String customerAddressIdGHN;
    private String note;
    private BigDecimal costShip;
    private BigDecimal discountCost;
}
