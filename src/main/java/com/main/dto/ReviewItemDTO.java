package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewItemDTO {
    private Integer ItemID;
    private String productName;
    private String imageItem;
    private String color;
    private String size;
}
