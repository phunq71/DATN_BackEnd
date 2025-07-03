package com.main.rest_controller;

import com.main.dto.Review_ReviewDTO;
import com.main.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewRestController {
    @Autowired
    private ReviewService reviewService;
    @GetMapping("/api/reviews")
    public ResponseEntity<List<Review_ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}
