package com.main.rest_controller;

import com.main.dto.ReviewImage_ReviewDTO;
import com.main.dto.Review_ReviewDTO;

import com.main.repository.OrderDetailRepository;
import com.main.repository.ReviewImageRepository;
import com.main.repository.ReviewRepository;
import com.main.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewRestController {
//    @Autowired
//    private ReviewService reviewService;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @GetMapping("/opulentia/{productID}")
    public ResponseEntity<Map<String, Object>> findAllReviews(@PathVariable String productID,
                                                              @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Review_ReviewDTO> reviewPage = reviewRepository.getReviewsByProductID(productID, pageable);
        reviewPage.getContent().forEach(dto -> {
            List<ReviewImage_ReviewDTO> images = reviewImageRepository.findByReviewID(dto.getReviewID());
            dto.setReviewImages(images);
        });
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewPage.getContent());
        response.put("totalPages", reviewPage.getTotalPages());
        response.put("totalItems", reviewPage.getTotalElements());
        response.put("currentPage", reviewPage.getNumber());

        return ResponseEntity.ok(response);
    }

}
