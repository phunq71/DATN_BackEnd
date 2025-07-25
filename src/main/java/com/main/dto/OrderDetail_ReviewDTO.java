package com.main.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Setter
@Getter
public class OrderDetail_ReviewDTO {
    private Integer orderDetailID;
    private Integer orderID;
    private Integer itemID;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
}
