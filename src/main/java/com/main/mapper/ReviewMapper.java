package com.main.mapper;

import com.main.dto.ReviewImage_ReviewDTO;
import com.main.dto.Review_ReviewDTO;
import com.main.entity.Review;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {
    public static Review_ReviewDTO toDTO(Review review) {
        if (review == null) return null;

        Review_ReviewDTO dto = new Review_ReviewDTO();
        dto.setCustomerName(review.getCustomer().getFullName());
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setCreateAt(review.getCreateAt());
        dto.setSize(review.getOrderDetail().getItem().getSize().getCode());
        dto.setColor(review.getOrderDetail().getItem().getVariant().getColor());
        if (review.getReviewImages() != null) {
            List<ReviewImage_ReviewDTO> imageDTOs = review.getReviewImages().stream()
                    .map(ReviewImageMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return dto;
    }
    public static List<Review_ReviewDTO> toListDTO(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) return new ArrayList<>();

        return reviews.stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());
    }
}
