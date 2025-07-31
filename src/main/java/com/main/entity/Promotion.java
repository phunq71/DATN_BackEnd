package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Promotions")
public class Promotion {

    @Id
    @Column(length = 8)
    private String promotionID;

    @Column(nullable = false, length = 95)
    private String promotionName;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(length = 55)
    private String banner;

    @Column(nullable = false)
    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @Column(nullable = false, length = 20)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membershipID")
    private Membership membership;

    @OneToMany(mappedBy = "promotion")
    private List<Voucher> vouchers;

    @OneToMany(mappedBy = "promotion")
    private  List<PromotionProduct> promotionProducts;
}
