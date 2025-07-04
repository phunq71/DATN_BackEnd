package com.main.service;

import com.main.dto.Review_ReviewDTO;
import com.main.entity.Review;

import java.util.List;

public interface ReviewService {
    List<Review_ReviewDTO> getAllReviews();
    List<Review_ReviewDTO> findAllReviews();
}
