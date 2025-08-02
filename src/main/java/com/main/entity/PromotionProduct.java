package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PromotionProducts")
public class PromotionProduct {

    @Id
    @Column(name = "PPID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promotionProductID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotionID", nullable = false)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productID", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantityUsed;

    @Column(nullable = false)
    private Integer quantityRemaining;

    @Column(nullable = false)
    private Byte discountPercent;

    @Column(length = 150)
    private String note;

    @OneToMany(mappedBy = "promotionProduct")
    private List<OrderDetail> orderDetails;

}
