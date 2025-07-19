package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewDTO {
    private Integer reviewID;
    private Integer orderDetailID;
    private String imageItem;
    private String productName;
    private BigDecimal price;
    private String color;
    private String size;
    private Integer rating;
    private List<String> reviewImages;
    private String reviewContent;
    private LocalDate createAt;
    private LocalDateTime orderUpdateAt;

    public ReviewDTO(Integer reviewID
            ,Integer orderDetailID
            , String imageItem
            , String productName
            , BigDecimal price
            , String color
            , String size
            , Integer rating
            , String reviewContent
            , LocalDate createAt
            , LocalDateTime orderUpdateAt) {
        this.reviewID=reviewID;
        this.orderDetailID = orderDetailID;
        this.imageItem = imageItem;
        this.productName = productName;
        this.price = price;
        this.color = color;
        this.size = size;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.createAt = createAt;
        this.orderUpdateAt = orderUpdateAt;
    }
}
