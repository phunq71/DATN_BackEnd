package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



@AllArgsConstructor @NoArgsConstructor @Data
@Entity
@Table(name = "vouchers")
public class Voucher implements Serializable {
    @Id
    @Column(length = 25)
    private String voucherID;

    @Column(nullable = false)
    private String discountType;  // BIT -> Boolean

    @Column(nullable = false)
    private Integer discountValue;

    @Column(precision = 24, scale = 2)
    private BigDecimal minOrderValue;

    @Column(nullable = false)
    private Integer quantityUsed;

    @Column(nullable = false)
    private Integer quantityRemaining;

    private LocalDateTime endDate;

    @Column(name = "Claim_conditions", precision = 24, scale = 2)
    private BigDecimal claimConditions;

    @ManyToOne
    @JoinColumn(name = "promotionID")
    private Promotion promotion;

    @OneToMany(mappedBy = "voucher")
    private List<UsedVoucher> usedVouchers;

    @OneToMany(mappedBy = "voucher")
    private List<Order> orders;

    private Boolean type;

}
