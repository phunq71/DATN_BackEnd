package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "OrderDetails")
public class OrderDetail {

    @Id
    @Column(name = "ODID")
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
    @JoinColumn(name = "PPID")
    private PromotionProduct promotionProduct;

    @OneToOne(mappedBy = "orderDetail")
    private ReturnItem returnItem;

    @OneToMany(mappedBy = "orderDetail", fetch = FetchType.LAZY)
    private List<Review> reviews;

    public OrderDetail(Integer orderDetailID) {
        this.orderDetailID = orderDetailID;
    }
}
