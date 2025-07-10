package com.main.service;

import com.main.dto.ReviewImage_ReviewDTO;

import java.util.List;

public interface ReviewImageService {
    List<ReviewImage_ReviewDTO> findByReviewID(Integer reviewID);
}
