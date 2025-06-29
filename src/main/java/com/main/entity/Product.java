package com.main.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "Products")
public class Product {
    @Id
    @Column(length = 10)
    private String productID;

    @Column(nullable = false, length = 150)
    private String productName;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Column(nullable = false, length = 15)
    private String targetCustomer;

    @Column(length = 45)
    private String brand;

    @ManyToOne
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<Variant> variants;

    @OneToMany(mappedBy = "product")
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "product")
    private List<PromotionProduct> promotionProducts;
}

