package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPreviewDTO {
    private Integer item_id;
    private String item_name;
    private String image_url;
    private String size;
    private String color;
    private  BigDecimal price;
    private Byte discountPercent;
    private Integer quantity;
    private BigDecimal total_price;
    private BigDecimal discountedPrice;
    private Integer PPID;
}
