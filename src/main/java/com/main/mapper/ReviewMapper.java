package com.main.mapper;

import com.main.dto.ReviewImage_ReviewDTO;
import com.main.dto.Review_ReviewDTO;
import com.main.entity.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {
    public static Review_ReviewDTO toDTO(Review review) {
        if (review == null) return null;

        Review_ReviewDTO dto = new Review_ReviewDTO();
        dto.setReviewID(review.getReviewID());
        dto.setCustomerID(review.getCustomer().getCustomerId());
        dto.setOrderDetailID(review.getOrderDetail().getOrderDetailID());
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setCreateAt(review.getCreateAt());

        if (review.getReviewImages() != null) {
            List<ReviewImage_ReviewDTO> imageDTOs = review.getReviewImages().stream()
                    .map(ReviewImageMapper::toDTO)
                    .collect(Collectors.toList());
            dto.setReviewImages(imageDTOs);
        }

        return dto;
    }
}
