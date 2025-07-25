package com.main.mapper;

import com.main.dto.ReviewImage_ReviewDTO;
import com.main.entity.ReviewImage;
import org.springframework.stereotype.Component;

@Component
public class ReviewImageMapper {
    public static ReviewImage_ReviewDTO toDTO(ReviewImage image) {
        if (image == null) return null;
        ReviewImage_ReviewDTO dto = new ReviewImage_ReviewDTO();
        dto.setImageUrl(image.getImageUrl());
        return dto;
    }
}
