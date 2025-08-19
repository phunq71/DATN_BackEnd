package com.main.service;

import com.main.dto.ReviewDTO;
import com.main.dto.ReviewStatsDTO;
import com.main.dto.Review_ReviewDTO;
import com.main.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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

    //------------------------------------------------------------------------------------------
    public ReviewDTO getReviewByCustomerIDAndODID(String customerID, Integer orderDetailID);

    public Review createReview(int orderDetailID,int rating, String content, List<MultipartFile> images, String customerID);

    public boolean deleteReview(int reviewID, String customerID);

    public Review updateReview(int reviewID, String customerID, int rating, String content, List<MultipartFile> insertedImages, List<String> deletedImages);
    public ReviewStatsDTO getReviewStats();
}
