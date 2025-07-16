package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderPriceDTO {
    private Integer orderId;
    private BigDecimal totalPrice;
    private BigDecimal productDiscount;
    private BigDecimal voucherDiscount;
    private BigDecimal  finalPrice;
}
