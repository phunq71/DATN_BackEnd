package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
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


    @Column(length = 100)
    private String addressIdGHN;

    @Column(length = 50)
    private String shippingCode;


    @Column(length = 300)
    private String note;

    @Column(nullable = false)
    private Boolean isOnline;

    @Column(length = 10)
    private String shipMethod;

    @Column(precision = 8, scale = 2)
    private BigDecimal costShip;

    @Column(precision = 8, scale = 2)
    private BigDecimal discountCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerID")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StaffID")
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VoucherID")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FacilityID", nullable = false)
    private Facility facility;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;


    @OneToOne(mappedBy = "order")
    private ReturnRequest returnRequest;

    @OneToOne(mappedBy = "order")
    private Transaction transaction;


    public Order(Integer orderID) {
        this.orderID = orderID;
    }


    public LocalDate delivery;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<LogOrders> logOrders;

}

