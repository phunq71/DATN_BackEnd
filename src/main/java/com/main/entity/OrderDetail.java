package com.main.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "OrderDetails")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderDetailID;

    @ManyToOne
    @JoinColumn(name = "OrderID", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "itemID", nullable = false)
    private Item item;

    @Column(precision = 12, scale = 2)
    private BigDecimal unitPrice;

    private Integer quantity;

    @Column(precision = 16, scale = 2)
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "PPID", nullable = false)
    private PromotionProduct promotionProduct;

    @OneToOne(mappedBy = "orderDetail")
    private ReturnItem returnItem;

    @OneToOne(mappedBy = "orderDetail")
    private Review review;
}
