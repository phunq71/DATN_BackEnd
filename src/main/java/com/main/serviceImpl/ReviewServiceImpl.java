package com.main.serviceImpl;

import com.main.dto.Review_ReviewDTO;
import com.main.mapper.ReviewMapper;
import com.main.repository.ReviewRepository;
import com.main.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Override
    public Page<Review_ReviewDTO> findFilteredReviews(String productID, Integer rating, String color, String size, Boolean hasImage, Pageable pageable) {
        return reviewRepository.findFilteredReviews(productID, rating, color, size, hasImage, pageable);
    }
    public Integer countReviewsByProductID(@Param("productID") String productID){
        return reviewRepository.countReviewsByProductID(productID);
    };
    @Override
    public Map<Integer, Integer> getReviewRatingCounts(String productId) {
        Map<Integer, Integer> ratingCounts = new HashMap<>();
        ratingCounts.put(5, reviewRepository.countRating5(productId));
        ratingCounts.put(4, reviewRepository.countRating4(productId));
        ratingCounts.put(3, reviewRepository.countRating3(productId));
        ratingCounts.put(2, reviewRepository.countRating2(productId));
        ratingCounts.put(1, reviewRepository.countRating1(productId));
        return ratingCounts;
    }
    public Double findAverageRatingByProductId(String productID){
        return reviewRepository.findAverageRatingByProductId(productID);
    };
}
