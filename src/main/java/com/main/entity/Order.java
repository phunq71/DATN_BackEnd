package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false, length = 45)
    private String status;

    @Column
    private LocalDateTime updateStatusAt;

    @Column(length = 150)
    private String shippingAddress;

    @Column(length = 300)
    private String note;

    @Column(nullable = false)
    private Boolean isOnline;

    @Column(length = 10)
    private String shipMethod;

    @Column(precision = 8, scale = 2)
    private BigDecimal costShip;

    @ManyToOne
    @JoinColumn(name = "CustomerID",nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "StaffID")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "VoucherID")
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "FacilityID", nullable = false)
    private Facility facility;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;


    @OneToOne(mappedBy = "order")
    private ReturnRequest returnRequest;

    @OneToOne(mappedBy = "order")
    private Transaction transaction;


    public Order(Integer orderID) {
        this.orderID = orderID;
    }
}

