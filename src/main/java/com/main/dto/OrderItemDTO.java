package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderItemDTO {
    private Integer itemID;
    private String productName;
    private String image;
    private String color;
    private String size;
    private BigDecimal price;
    private Byte discountPercent;
    private Integer quantity;
    private Boolean isReviewed;
    private Integer orderDetailID;
}
