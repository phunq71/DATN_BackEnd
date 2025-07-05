package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class ProductByCategory {
    private String productID;
    private String productName;
    private String mainImageUrl;
    private BigDecimal price;
    private List<String> colors;
    private String variantMainID;
}
