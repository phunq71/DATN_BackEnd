package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdManagement_ProductDTO {
    private String productName;
    private String image;
    private Byte discountPercent;
    private BigDecimal originalPrice;
    private Integer quantity;
}
