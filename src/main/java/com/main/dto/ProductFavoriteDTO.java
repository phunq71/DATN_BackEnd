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

    // bổ sung
    private Byte discountPercent;
    private Boolean isHot;
    private Boolean isNew;
    private Integer totalLikes;
    private Double avg_rating;
    private List<ColorOption> options;


    private String color;
    private String targetCustomer;
    private boolean isFavorite = true;
    private String parentCategory;
    private String childCategory;


    public ProductFavoriteDTO(String productID, String productName, String mainImageUrl, BigDecimal price, String color, String targetCustomer, boolean isFavorite, String parentCategory, String childCategory) {
        this.productID = productID;
        this.productName = productName;
        this.mainImageUrl = mainImageUrl;
        this.price = price;
        this.color = color;
        this.targetCustomer = targetCustomer;
        this.isFavorite = isFavorite;
        this.parentCategory = parentCategory;
        this.childCategory = childCategory;

    }
}
