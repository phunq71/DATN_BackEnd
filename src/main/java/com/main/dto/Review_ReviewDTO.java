package com.main.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class Review_ReviewDTO {
    private Integer reviewID;
    private String customerName;
    private String content;
    private Integer rating;
    private LocalDate createAt;
    private String size;
    private String color;
    private List<ReviewImage_ReviewDTO> reviewImages;

    public Review_ReviewDTO( final Integer reviewID, final String customerName, final String content, final Integer rating, final LocalDate createAt, final String size, final String color) {
        this.reviewID = reviewID;
        this.customerName = customerName;
        this.content = content;
        this.rating = rating;
        this.createAt = createAt;
        this.size = size;
        this.color = color;
    }
}
