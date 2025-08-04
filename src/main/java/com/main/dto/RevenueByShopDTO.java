package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RevenueByShopDTO {
    private String area;
    private String shop;
    private BigDecimal revenue;
}
