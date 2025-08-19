package com.main.repository;

import com.main.dto.ReviewDTO;
import com.main.dto.ReviewItemDTO;
import com.main.dto.Review_ReviewDTO;
import com.main.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
// hàm tim đánh giá theo tham số và phân trang
@Query("""
    SELECT new com.main.dto.Review_ReviewDTO(r.reviewID, c.fullName, r.content, r.rating, r.createAt, v.color, s.code)
    FROM Product p
    JOIN p.variants v
    JOIN v.items i
    JOIN i.size s
    JOIN i.orderDetails odt
    JOIN odt.reviews r
    JOIN r.customer c
    WHERE p.productID = :productID
    AND (:rating IS NULL OR r.rating = :rating)
    AND (:color IS NULL OR v.color = :color)
    AND (:size IS NULL OR s.code = :size)
    AND (:hasImage IS NULL OR
         (:hasImage = true AND EXISTS (
            SELECT ri FROM ReviewImage ri WHERE ri.review.reviewID = r.reviewID
         ))
    )
""")
Page<Review_ReviewDTO> findFilteredReviews(@Param("productID") String productID,
                                           @Param("rating") Integer rating,
                                           @Param("color") String color,
                                           @Param("size") String size,
                                           @Param("hasImage") Boolean hasImage,
                                           Pageable pageable);
    @Query("""
    SELECT COUNT(r)
    FROM Product p
    JOIN p.variants v
    JOIN v.items i
    JOIN i.orderDetails odt
    JOIN odt.reviews r
    WHERE p.productID = :productID
""")
    Integer countReviewsByProductID(@Param("productID") String productID);
    @Query("""
        SELECT COUNT(r)
        FROM Product p
        JOIN p.variants v
        JOIN v.items i
        JOIN i.orderDetails odt
        JOIN odt.reviews r
        WHERE p.productID = :productID AND r.rating = 5
    """)
    Integer countRating5(@Param("productID") String productID);

    @Query("""
        SELECT COUNT(r)
        FROM Product p
        JOIN p.variants v
        JOIN v.items i
        JOIN i.orderDetails odt
        JOIN odt.reviews r
        WHERE p.productID = :productID AND r.rating = 4
    """)
    Integer countRating4(@Param("productID") String productID);

    @Query("""
        SELECT COUNT(r)
        FROM Product p
        JOIN p.variants v
        JOIN v.items i
        JOIN i.orderDetails odt
        JOIN odt.reviews r
        WHERE p.productID = :productID AND r.rating = 3
    """)
    Integer countRating3(@Param("productID") String productID);

    @Query("""
        SELECT COUNT(r)
        FROM Product p
        JOIN p.variants v
        JOIN v.items i
        JOIN i.orderDetails odt
        JOIN odt.reviews r
        WHERE p.productID = :productID AND r.rating = 2
    """)
    Integer countRating2(@Param("productID") String productID);

    @Query("""
        SELECT COUNT(r)
        FROM Product p
        JOIN p.variants v
        JOIN v.items i
        JOIN i.orderDetails odt
        JOIN odt.reviews r
        WHERE p.productID = :productID AND r.rating = 1
    """)
    Integer countRating1(@Param("productID") String productID);

    @Query("""
        SELECT AVG(r.rating)
        FROM Product p
        JOIN p.variants v
        JOIN v.items i
        JOIN i.orderDetails odt
        JOIN odt.reviews r
        WHERE p.productID = :productID
    """)
    Double findAverageRatingByProductId(@Param("productID") String productID);


    //-------------------------------------------------------------------------------------------------
    @Query("""
    SELECT r.reviewID
    , r.orderDetail.orderDetailID
    , (SELECT img.imageUrl AS image FROM Image img WHERE img.variant = r.orderDetail.item.variant AND img.isMainImage = true)
    , r.orderDetail.item.variant.product.productName
    , r.orderDetail.unitPrice
    , r.orderDetail.item.variant.color
    , r.orderDetail.item.size.code
    , r.rating
    , r.content
    , r.createAt
    , r.orderDetail.order.updateStatusAt
    FROM Review r
    WHERE r.orderDetail.orderDetailID = :orderDetailID
    AND r.customer.customerId=:customerID
    """)
    ReviewDTO getReviewByCustomerIdAndODID(@Param("customerID") String customerID,@Param("orderDetailID") Integer orderItemID);


    @Query("""
    SELECT ri.imageUrl
    FROM ReviewImage ri
    WHERE ri.review.reviewID=:reviewID
    """)
    List<String> getReviewImagesByReviewID(@Param("reviewID") Integer reviewID);


    @Query("""
    SELECT i.itemId
    , i.variant.product.productName
    , (SELECT img.imageUrl AS image FROM Image img WHERE img.variant = i.variant AND img.isMainImage = true)
    , i.variant.color
    , i.size.code
    FROM Item i
    WHERE i.itemId=:itemID
    """)
    ReviewItemDTO getReviewItemByItemID(@Param("itemID") Integer itemID);

    boolean existsByCustomer_CustomerIdAndReviewID(String customerID, Integer reviewID);

    @Query("""
    SELECT reveiw
    from Review reveiw
    where reveiw.customer.customerId=:customerID
    """)
    public List<Review> findReviewByCustomer(String customerID);
    // Điểm trung bình toàn hệ thống
    @Query("SELECT ROUND(AVG(r.rating), 1) FROM Review r")
    Double getAverageRating();

    // Đếm số lượng mỗi mức sao
    @Query("SELECT r.rating, COUNT(r) FROM Review r GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getStarCounts();
}
