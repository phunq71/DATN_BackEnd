package com.main.serviceImpl;

import com.main.dto.Review_ReviewDTO;
import com.main.entity.Review;
import com.main.mapper.ReviewMapper;
import com.main.repository.ReviewRepository;
import com.main.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public List<Review_ReviewDTO> findAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<Review_ReviewDTO> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());
    }
}
