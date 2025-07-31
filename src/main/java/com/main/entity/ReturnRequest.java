package com.main.entity;

import jakarta.persistence.*;
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
    @Column(name = "RRID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer returnRequestID;

    @OneToOne
    @JoinColumn(name = "orderID", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staffID")
    private Staff staff;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(nullable = false)
    private Boolean isOnline;

    @Column(length = 500)
    private String note;

    @OneToMany(mappedBy = "returnRequest")
    private List<ReturnItem> returnItems;

    @OneToOne(mappedBy = "returnRequest")
    private Transaction transaction;

    public ReturnRequest(Integer returnRequestID, Order order, LocalDateTime requestDate, String status, Boolean isOnline) {
        this.returnRequestID = returnRequestID;
        this.order = order;
        this.requestDate = requestDate;
        this.status = status;
        this.isOnline = isOnline;
    }
}
