package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ReturnItemDTO {
    private Integer orderDetailID;
    private String image;
    private String productName;
    private String size;
    private String color;
    private BigDecimal price;
    private Byte discountPercent;
    private Integer quantity;

    private String reason;
    private Integer returnItemID;

    private List<String> evidenceImages;

    public ReturnItemDTO(Integer orderDetailID, String image, String productName, String size, String color, BigDecimal price, Byte discountPercent, Integer quantity) {
        this.orderDetailID = orderDetailID;
        this.image = image;
        this.productName = productName;
        this.size = size;
        this.color = color;
        this.price = price;
        this.discountPercent = discountPercent;
        this.quantity = quantity;
    }

    public ReturnItemDTO(Integer orderDetailID, String image, String productName, String size, String color, BigDecimal price, Byte discountPercent, Integer quantity, String reason, Integer returnItemID) {
        this.orderDetailID = orderDetailID;
        this.image = image;
        this.productName = productName;
        this.size = size;
        this.color = color;
        this.price = price;
        this.discountPercent = discountPercent;
        this.quantity = quantity;
        this.reason = reason;
        this.returnItemID = returnItemID;
        this.evidenceImages = new ArrayList<>();
    }
}
