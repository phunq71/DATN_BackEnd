package com.main.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "PromotionProducts")
public class PromotionProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promotionProductID;

    @ManyToOne
    @JoinColumn(name = "promotionID", nullable = false)
    private Promotion promotion;

    @ManyToOne
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
