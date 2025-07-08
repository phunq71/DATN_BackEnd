package com.main.service;

import com.main.dto.Review_ReviewDTO;
import com.main.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    Page<Review_ReviewDTO> findFilteredReviews(String productID,
                                               Integer rating,
                                               String color,
                                               String size,
                                               Boolean hasImage,
                                               Pageable pageable);
    Integer countReviewsByProductID(String productID);
    Map<Integer, Integer> getReviewRatingCounts(String productId);
    Double findAverageRatingByProductId(String productID);
}
