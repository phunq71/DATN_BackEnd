package com.main.repository;

import com.main.dto.PromotionVoucherManagerDTO;
import com.main.entity.Promotion;
import com.main.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;


import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {

    @Query("""
    SELECT pm.promotionID
        , pm.promotionName
        , pm.description
        , pm.startDate
        , pm.endDate
        , pm.banner
        , pm.createAt
        , pm.updateAt
        , pm.type
        , me.membershipId
        , me.rank
        FROM Promotion pm
        LEFT JOIN pm.membership me
        ORDER BY pm.createAt DESC
    """)
    public List<PromotionVoucherManagerDTO> getPromotions();

//Hamf nay repon ho tro lay id cao nhat
    Promotion findTop1ByOrderByPromotionIDDesc();

    @Query("""
        SELECT p
        FROM Promotion p
        WHERE p.startDate < :endDate AND p.endDate > :startDate
            AND p.type = 'ProductDiscount'

    """)
    public List<Promotion> findPromotionByStartDateAndEndDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
            );



    @Query("""
    SELECT p
    FROM Promotion p
    WHERE p.type = 'NewRank'
    AND CURRENT_TIMESTAMP BETWEEN p.startDate AND p.endDate
    AND p.membership.membershipId = :membershipId
""")
    List<Promotion> findByNewRank(@Param("membershipId") String menbershipId);


}
