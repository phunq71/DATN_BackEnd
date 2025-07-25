package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@Table(name = "ReturnItems")
@AllArgsConstructor
@NoArgsConstructor
public class ReturnItem {

    @Id
    @Column(name = "RIID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer returnItemId;

    @ManyToOne
    @JoinColumn(name = "rrid", nullable = false)
    private ReturnRequest returnRequest;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 500)
    private String reason;

    @OneToOne
    @JoinColumn(name = "OrderDetailID", nullable = false)
    private OrderDetail orderDetail;

    @OneToMany(mappedBy = "returnItem")
    private List<ReviewImage> reviewImages;


    public ReturnItem(Integer returnItemId) {
        this.returnItemId = returnItemId;
    }
}
