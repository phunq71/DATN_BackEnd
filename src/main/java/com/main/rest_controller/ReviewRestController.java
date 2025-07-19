package com.main.rest_controller;

import com.main.dto.ReviewDTO;
import com.main.dto.ReviewImage_ReviewDTO;
import com.main.dto.Review_ReviewDTO;
import com.main.entity.OrderDetail;
import com.main.repository.ReviewRepository;
import com.main.service.OrderService;
import com.main.service.ReviewImageService;
import com.main.service.ReviewService;
import com.main.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewRestController {
    private final ReviewService reviewService;
    private final ReviewImageService reviewImageService;
    private final OrderService orderService;

    public ReviewRestController(ReviewService reviewService, ReviewImageService reviewImageService, OrderService orderService) {
        this.reviewService = reviewService;
        this.reviewImageService = reviewImageService;
        this.orderService = orderService;
    }


    @GetMapping("/opulentia/{productID}")
    public ResponseEntity<Map<String, Object>> findFilteredReviews(
            @PathVariable String productID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) Boolean hasImage
    ) {
//        System.out.println(productID);
//        System.out.println(rating);
//        System.out.println(color);
//        System.out.println(size);
//        System.out.println(hasImage);
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
//------------------------------------------------------------------------------
    @GetMapping("/opulentia_user/review/get/{orderDetailID}")
    public ResponseEntity<?> getReviewByODID(@PathVariable Integer orderDetailID) {
        String accountId = AuthUtil.getAccountID();

        if (accountId == null) {
            return new ResponseEntity<>(Map.of(
                    "status", "UNAUTHORIZED",
                    "data", ""
            ), HttpStatus.UNAUTHORIZED);
        }

        boolean isOwned = orderService.checkOrderDetailByCustomerIDAndODID(accountId, orderDetailID);
        if (!isOwned) {
            return new ResponseEntity<>(Map.of(
                    "status", "FORBIDDEN",
                    "data", ""
            ), HttpStatus.FORBIDDEN);
        }


        return orderService.handleReviewAccess(orderDetailID, accountId);
    }

    @PostMapping("/opulentia_user/review/create")
    public ResponseEntity<String> createReview(
            @RequestParam("orderDetailID") int orderDetailID,
            @RequestParam("rating") int rating,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

//        // In ra thông tin nhận được
//        System.out.println("=== Thông tin đánh giá nhận được ===");
//        System.err.println(orderDetailID);
//        System.out.println("Rating: " + rating);
//        System.out.println("Content: " + content);
//
//        if (images != null && !images.isEmpty()) {
//            System.out.println("Số lượng ảnh: " + images.size());
//            for (int i = 0; i < images.size(); i++) {
//                System.out.println("Ảnh " + (i + 1) + ": " + images.get(i).getOriginalFilename());
//            }
//        } else {
//            System.out.println("Không có ảnh đính kèm");
//        }

        String accountId = AuthUtil.getAccountID();

        if (accountId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {

            if(reviewService.createReview(orderDetailID, rating, content, images, accountId)==null) {throw new Exception();};
        }catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }


        // Trả về response thành công
        return ResponseEntity.ok("Đã nhận đánh giá thành công!");
    }

    @DeleteMapping("/opulentia_user/review/delete/{reviewID}")
    public ResponseEntity<Boolean> deleteReview(@PathVariable Integer reviewID) {
        String accountId = AuthUtil.getAccountID();

        if (accountId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(reviewService.deleteReview(reviewID, accountId));
    }

    @PutMapping("/opulentia_user/review/update")
    public ResponseEntity<String> updateReview(
            @RequestParam("reviewID") int reviewID,
            @RequestParam("rating") int rating,
            @RequestParam("content") String content,
            @RequestParam(value = "insertedImages", required = false) List<MultipartFile> insertedImages,
            @RequestParam(value = "deletedImages" , required = false) List<String> deletedImages
    ){
        String accountId = AuthUtil.getAccountID();

        if (accountId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


//                // In ra thông tin nhận được
//        System.out.println("=== Thông tin đánh giá nhận được ===");
//        System.err.println(orderDetailID);
//        System.out.println("Rating: " + rating);
//        System.out.println("Content: " + content);
//
//        if (insertedImages != null && !insertedImages.isEmpty()) {
//            System.out.println("Số lượng ảnh: " + insertedImages.size());
//            for (int i = 0; i < insertedImages.size(); i++) {
//                System.out.println("Ảnh " + (i + 1) + ": " + insertedImages.get(i).getOriginalFilename());
//            }
//        } else {
//            System.out.println("Không có ảnh đính kèm");
//        }
//
//        if (deletedImages != null && !deletedImages.isEmpty()) {
//            System.out.println("Số ảnh cần xóa: " + deletedImages.size());
//            deletedImages.forEach(System.out::println);
//        } else System.out.println("Không có ảnh cần xóa");

        reviewService.updateReview(reviewID, accountId, rating, content, insertedImages, deletedImages);

        return ResponseEntity.ok("Update thành công");
    }
}

