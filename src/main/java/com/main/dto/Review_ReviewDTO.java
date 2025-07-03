package com.main.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class Review_ReviewDTO {
    private Integer reviewID;
    private String customerID;
    private Integer orderDetailID;
    private String content;
    private Integer rating;
    private LocalDate createAt;
    private List<ReviewImage_ReviewDTO> reviewImages;
}
