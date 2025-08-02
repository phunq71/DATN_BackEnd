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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "reviewID")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returnItemID")
    private ReturnItem returnItem;

    public ReviewImage(Review review, String imageUrl) {
        this.review = review;
        this.imageUrl = imageUrl;
    }

    public ReviewImage(Integer returnItemID, String imageUrl) {
        this.imageUrl = imageUrl;
        this.returnItem = new ReturnItem(returnItemID);
    }
}
