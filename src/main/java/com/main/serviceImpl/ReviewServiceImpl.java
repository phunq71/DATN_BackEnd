package com.main.serviceImpl;

import com.main.dto.ReviewDTO;
import com.main.dto.ReviewStatsDTO;
import com.main.dto.Review_ReviewDTO;
import com.main.dto.StarCountDTO;
import com.main.entity.*;
import com.main.mapper.ReviewMapper;
import com.main.repository.OrderDetailRepository;
import com.main.repository.OrderRepository;
import com.main.repository.ReviewImageRepository;
import com.main.repository.ReviewRepository;
import com.main.service.ReviewService;
import com.main.utils.FileUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final FileUtil fileUtil;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewImageRepository reviewImageRepository, OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, FileUtil fileUtil) {
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.fileUtil = fileUtil;
    }



    @Override
    public Page<Review_ReviewDTO> findFilteredReviews(String productID, Integer rating, String color, String size, Boolean hasImage, Pageable pageable) {
        return reviewRepository.findFilteredReviews(productID, rating, color, size, hasImage, pageable);
    }
    public Integer countReviewsByProductID(String productID){
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
    }

    @Override
    public ReviewDTO getReviewByCustomerIDAndODID(String customerID, Integer orderDetailID) {
        ReviewDTO reviewDTO = reviewRepository.getReviewByCustomerIdAndODID(customerID, orderDetailID);
        if (reviewDTO == null) {return null;}

        reviewDTO.setReviewImages(reviewRepository.getReviewImagesByReviewID(reviewDTO.getReviewID()));
        return reviewDTO;
    }

    @Override
    public Review createReview(int orderDetailID,int rating, String content, List<MultipartFile> images, String customerID) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailID).get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime statusAt = orderDetail.getOrder().getUpdateStatusAt();

        // Tính số ngày giữa 2 thời điểm
        long daysBetween = ChronoUnit.DAYS.between(statusAt, now);

        if (daysBetween < 15) {
            Review review = new Review();

            Customer customer = new Customer(customerID);


            review.setOrderDetail(orderDetail);
            review.setRating(rating);
            review.setContent(content);
            review.setCustomer(customer);
            review.setCreateAt(LocalDate.now());
            Review savedReview = reviewRepository.save(review);

            images.forEach(image -> {
                try {
                    String fileName = fileUtil.saveImage(image);
                    ReviewImage reviewImage = new ReviewImage(savedReview, fileName);

                    reviewImageRepository.save(reviewImage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            return savedReview;
        }
        return null;
    }

//    @Override
//    public boolean deleteReview(int reviewID, String customerID) {
//        if(reviewRepository.existsByCustomer_CustomerIdAndReviewID(customerID, reviewID)) {
//            Review review = reviewRepository.findById(reviewID).get();
//            LocalDateTime statusDate = review.getOrderDetail().getOrder().getUpdateStatusAt();
//            long daysBetween = ChronoUnit.DAYS.between(statusDate, LocalDateTime.now());
//
//            if (daysBetween < 15) {
//                //xóa reviewImage
//                List<String> images = review.getReviewImages().stream().map(ReviewImage::getImageUrl).toList();
//                images.forEach(FileUtil::deleteFile);
//                reviewImageRepository.deleteByReviewID(reviewID);
//
//                //xóa review
//                reviewRepository.deleteById(reviewID);
//                return true;
//            }else System.err.println("đã quá 15 ngày chỉnh sửa");
//        }
//
//        return false;
//    }

    @Override
    @Transactional
    public boolean deleteReview(int reviewID, String customerID) {
        //System.err.println("Bắt đầu deleteReview, reviewID = " + reviewID + ", customerID = " + customerID);

        if (reviewRepository.existsByCustomer_CustomerIdAndReviewID(customerID, reviewID)) {
           // System.err.println("Review tồn tại và thuộc về customer");

            Review review = reviewRepository.findById(reviewID).orElse(null);
            if (review == null) {
         //       System.err.println("Không tìm thấy review khi findById, return false");
                return false;
            }
          //  System.err.println("Tìm thấy review: " + review.getReviewID());

            OrderDetail orderDetail = review.getOrderDetail();
            if (orderDetail == null) {
          //      System.err.println("OrderDetail null -> không thể tiếp tục");
                return false;
            }
         //   System.err.println("OrderDetail: " + orderDetail.getOrderDetailID());

            Order order = orderDetail.getOrder();
            if (order == null) {
          //      System.err.println("Order null -> không thể tiếp tục");
                return false;
            }
         //   System.err.println("Order: " + order.getStatus());

            LocalDateTime statusDate = order.getUpdateStatusAt();
         //   System.err.println("UpdateStatusAt: " + statusDate);

            long daysBetween = ChronoUnit.DAYS.between(statusDate, LocalDateTime.now());
         //   System.err.println("daysBetween: " + daysBetween);

            if (daysBetween < 15) {
         //       System.err.println("Chưa quá 15 ngày => tiếp tục xóa");

                // Xóa file ảnh
                List<String> images = review.getReviewImages().stream().map(ReviewImage::getImageUrl).toList();
        //        System.err.println("Danh sách ảnh: " + images);

                images.forEach(image -> {
        //            System.err.println("Xóa file: " + image);
                    fileUtil.deleteFile(image);
                });

                //reviewImageRepository.deleteByReviewID(reviewID);
       //         System.err.println("Đã xóa record reviewImage trong DB");

                reviewRepository.deleteById(reviewID);
       //         System.err.println("Đã xóa review");

                return true;
            } else {
                System.err.println("Đã quá 15 ngày chỉnh sửa, không được xóa");
            }
        } else {
            System.err.println("Review không tồn tại hoặc không thuộc customer này");
        }

        return false;
    }


    @Override
    public Review updateReview(int reviewID, String customerID, int rating, String content,
                               List<MultipartFile> insertedImages, List<String> deletedImages) {
        if (!reviewRepository.existsByCustomer_CustomerIdAndReviewID(customerID, reviewID)) {
            System.err.println("Customer không sở hữu review này!");
            return null;
        }

        Review review = reviewRepository.findById(reviewID).orElse(null);
        if (review == null) {
            return null;
        }

        LocalDateTime statusDate = review.getOrderDetail().getOrder().getUpdateStatusAt();
        long daysBetween = ChronoUnit.DAYS.between(statusDate, LocalDateTime.now());

        if (daysBetween >= 15) {
            System.err.println("Quá 15 ngày chỉnh sửa");
            return null;
        }

        // Cập nhật thông tin cơ bản
        review.setRating(rating);
        review.setContent(content);

        // Xử lý ảnh
        List<ReviewImage> currentImages = review.getReviewImages();

        // 1. Xóa các ảnh đã bị xóa
        if (deletedImages != null && !deletedImages.isEmpty()) {
            // Xóa từ database
            List<ReviewImage> imagesToDelete = currentImages.stream()
                    .filter(img -> deletedImages.contains(img.getImageUrl()))
                    .collect(Collectors.toList());

            reviewImageRepository.deleteAll(imagesToDelete);

            // Xóa file vật lý
            deletedImages.forEach(fileUtil::deleteFile);

            // Cập nhật lại danh sách ảnh hiện tại
            currentImages.removeAll(imagesToDelete);
        }

        // 2. Thêm các ảnh mới
        if (insertedImages != null && !insertedImages.isEmpty()) {
            List<ReviewImage> newImages = new ArrayList<>();

            for (MultipartFile image : insertedImages) {
                try {
                    String imageUrl = fileUtil.saveImage(image);
                    ReviewImage reviewImage = new ReviewImage(review, imageUrl);
                    newImages.add(reviewImage);
                } catch (IOException e) {
                    System.err.println("Lỗi khi lưu ảnh: " + e.getMessage());
                }
            }

            reviewImageRepository.saveAll(newImages);
            currentImages.addAll(newImages);
        }

        return reviewRepository.save(review);
    }

    @Override
    public ReviewStatsDTO getReviewStats() {
        Double avgRating = reviewRepository.getAverageRating();
        List<Object[]> counts = reviewRepository.getStarCounts();

        // Tạo danh sách đủ 5 sao
        List<StarCountDTO> starCountList = new ArrayList<>();
        for (int i = 5; i >= 1; i--) {
            long count = 0;
            for (Object[] row : counts) {
                if (((Integer) row[0]) == i) {
                    count = (Long) row[1];
                    break;
                }
            }
            starCountList.add(new StarCountDTO(i, count));
        }

        return new ReviewStatsDTO(avgRating != null ? avgRating : 0.0, starCountList);
    }
}
