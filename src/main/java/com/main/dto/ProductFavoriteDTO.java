package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data @NoArgsConstructor
@AllArgsConstructor
public class ProductFavoriteDTO {
    private String productID;
    private String productName;
    private String mainImageUrl;
    private BigDecimal price;
    private String color;
    private String targetCustomer;
    private boolean isFavorite = true;
    private String parentCategory;
    private String childCategory;
}
