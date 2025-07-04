package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ReviewImages")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewImage {
    @Id
    @Column( name = "RIID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewImageID;

    @Column(nullable = false, length = 55)
    private String imageUrl;

    @ManyToOne
    @JoinColumn( name = "reviewID", nullable = false)
    private Review review;


}
