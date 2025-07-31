package com.main.repository;

import com.main.entity.Voucher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {
        @Query("""
    SELECT v FROM Voucher v
    JOIN v.promotion p
    WHERE (p.membership.membershipId IS NULL OR p.membership.membershipId = :membershipID)
      AND CURRENT_TIMESTAMP BETWEEN p.startDate AND p.endDate
      AND v.quantityRemaining > 0
      AND NOT EXISTS (
          SELECT uv FROM UsedVoucher uv
          WHERE uv.voucher.voucherID = v.voucherID
            AND uv.customer.customerId = :customerID
            AND uv.type = TRUE
      )
      AND v.type = TRUE
    """)
        List<Voucher> findValidVouchersForCustomer(
                @Param("membershipID") String membershipID,
                @Param("customerID") String customerID
        );

        @Query("""
    SELECT uv.voucher
    FROM UsedVoucher uv
    WHERE uv.customer.customerId = :customerID
      AND uv.type = FALSE
      AND (uv.voucher.endDate IS NULL OR uv.voucher.endDate >= CURRENT_TIMESTAMP)
    """)
        List<Voucher> findUnclaimedVouchersByCustomer(@Param("customerID") String customerID);

    // Lấy hậu mãi gần nhất sắp đạt được
    @Query("""
    SELECT v
    FROM Voucher v
    JOIN v.promotion p
    WHERE (p.membership.membershipId IS NULL OR p.membership.membershipId = :idMembership)
      AND CURRENT_TIMESTAMP BETWEEN p.startDate AND p.endDate
      AND p.type = 'AfterSaleService'
      AND NOT EXISTS (
          SELECT uv FROM UsedVoucher uv
          WHERE uv.voucher.voucherID = v.voucherID
            AND uv.customer.customerId = :customerID
      )
      AND v.claimConditions > :totalAmount
""")
    List<Voucher> findNearestVoucherByMembershipJPQL(
            @Param("idMembership") String idMembership,
            @Param("totalAmount") BigDecimal totalAmount,
            @Param("customerID") String customerID
    );



    // Lấy hậu mãi đã đạt được
    @Query("""
    SELECT v
    FROM Voucher v
    JOIN v.promotion p
    WHERE (p.membership.membershipId IS NULL OR p.membership.membershipId = :idMembership)
      AND CURRENT_TIMESTAMP BETWEEN p.startDate AND p.endDate
      AND p.type = 'AfterSaleService'
      AND v.claimConditions <= :totalAmount
      AND NOT EXISTS (
          SELECT uv FROM UsedVoucher uv
          WHERE uv.voucher.voucherID = v.voucherID
            AND uv.customer.customerId = :customerID
      )
""")
    List<Voucher> findBestMatchedVoucher(
            @Param("idMembership") String idMembership,
            @Param("totalAmount") BigDecimal totalAmount,
            @Param("customerID") String customerID
    );


}
