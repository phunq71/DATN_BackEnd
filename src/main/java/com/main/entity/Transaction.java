package com.main.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transactions")
@Data
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionID;

    @Column(length = 50)
    private String paymentCode;

    @Column(nullable = false)
    private Boolean transactionType;

    @Column(precision = 24, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(length = 20)
    private String paymentMethod; // VNPay/MOMO/SEPAY

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(length = 100)
    private String description;

    @Column(length = 20, nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name ="staffId")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name ="approver")
    private Staff approver;

    @OneToOne
    @JoinColumn(name = "returnRequestId")
    private ReturnRequest returnRequest;

    @OneToOne
    @JoinColumn(name = "orderID")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "facilityId")
    private Facility facility;

}