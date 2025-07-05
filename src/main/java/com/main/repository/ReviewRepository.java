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
    @Query("""
    SELECT new com.main.dto.Review_ReviewDTO(
       r.reviewID, c.fullName, r.content, r.rating, r.createAt, i.size.code, v.color
    )
    FROM Review r
    JOIN r.customer c
    JOIN r.orderDetail od
    JOIN od.item i
    JOIN i.variant v
    WHERE v.product.productID = :productID
""")
    Page<Review_ReviewDTO> getReviewsByProductID(@Param("productID") String productID, Pageable pageable);
}
