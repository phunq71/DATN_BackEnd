package com.main.dto;

import com.main.entity.Category;
import com.main.entity.Favorite;
import com.main.entity.PromotionProduct;
import com.main.entity.Variant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CusManagement_productDTO {
    private String productID;
    private String productName;
    private String color;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Integer rating;
    private String content;
}
