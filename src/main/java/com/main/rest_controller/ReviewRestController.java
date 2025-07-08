package com.main.rest_controller;

import com.main.dto.ReviewImage_ReviewDTO;
import com.main.dto.Review_ReviewDTO;
import com.main.repository.ReviewRepository;
import com.main.service.ReviewImageService;
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
    @Autowired
    ReviewService reviewService;
    @Autowired
    private ReviewImageService reviewImageService;

    @GetMapping("/opulentia/{productID}")
    public ResponseEntity<Map<String, Object>> findFilteredReviews(
            @PathVariable String productID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) Boolean hasImage
    ) {
        System.out.println(productID);
        System.out.println(rating);
        System.out.println(color);
        System.out.println(size);
        System.out.println(hasImage);
        Pageable pageable = PageRequest.of(page, 5);

        // Gọi repository có truyền các filter
        Page<Review_ReviewDTO> reviewPage = reviewService.findFilteredReviews(
                productID, rating, color, size, hasImage, pageable
        );

        // Lấy ảnh nếu có
        reviewPage.getContent().forEach(dto -> {
            List<ReviewImage_ReviewDTO> images = reviewImageService.findByReviewID(dto.getReviewID());
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
