package com.main.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ReturnRequests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer returnRequestID;

    @OneToOne
    @JoinColumn(name = "orderID", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "staffID", nullable = false)
    private Staff staff;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(nullable = false)
    private Boolean isOnline;

    @Column(length = 500, nullable = false)
    private String note;

    @ManyToOne
    @JoinColumn(name = "StaffID", nullable = false)
    private Staff staff;

    @OneToMany(mappedBy = "returnRequest")
    private List<ReturnItem> returnItems;

    @OneToOne(mappedBy = "returnRequest")
    private Transaction transaction;

}
