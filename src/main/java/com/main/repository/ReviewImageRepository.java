package com.main.repository;

import com.main.dto.ReviewImage_ReviewDTO;
import com.main.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {
    @Query("""
        SELECT new com.main.dto.ReviewImage_ReviewDTO(ri.imageUrl)
        FROM ReviewImage ri
        WHERE ri.review.reviewID = :reviewID
    """)
    List<ReviewImage_ReviewDTO> findByReviewID(@Param("reviewID") Integer reviewID);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReviewImage ri " +
            "WHERE ri.review.reviewID = :reviewID")
    void deleteByReviewID(@Param("reviewID") Integer reviewID);
}
