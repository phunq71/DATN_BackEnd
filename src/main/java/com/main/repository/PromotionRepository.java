package com.main.repository;

import com.main.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {


    @Query("""
    SELECT p
    FROM Promotion p
    WHERE p.type = 'NewRank'
    AND CURRENT_TIMESTAMP BETWEEN p.startDate AND p.endDate
    AND p.membership.membershipId = :membershipId
""")
    List<Promotion> findByNewRank(@Param("membershipId") String menbershipId);

}
