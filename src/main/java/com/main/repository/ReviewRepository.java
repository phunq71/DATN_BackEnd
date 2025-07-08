package com.main.repository;

import com.main.dto.Review_ReviewDTO;
import com.main.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
