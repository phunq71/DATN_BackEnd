package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Table(name = "ReturnItems")
@AllArgsConstructor
@NoArgsConstructor
public class ReturnItem {

    @Id
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
}
