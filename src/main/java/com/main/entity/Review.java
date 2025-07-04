package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Reviews")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewID;

    @ManyToOne
    @JoinColumn(name = "CustomerID")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ODID", nullable = false)
    private OrderDetail orderDetail;

    @Column(length = 150, nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private LocalDate createAt;

    @OneToMany(mappedBy = "review")
    private List<ReviewImage> reviewImages;
}
