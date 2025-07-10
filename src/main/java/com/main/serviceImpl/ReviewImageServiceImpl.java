package com.main.serviceImpl;

import com.main.dto.ReviewImage_ReviewDTO;
import com.main.repository.ReviewImageRepository;
import com.main.service.ReviewImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewImageServiceImpl implements ReviewImageService {
    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @Override
    public List<ReviewImage_ReviewDTO> findByReviewID(Integer reviewID) {
        return reviewImageRepository.findByReviewID(reviewID);
    }
}
