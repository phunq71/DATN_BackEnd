package com.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.main.entity.Product;
import com.main.entity.Image;
import com.main.entity.Item;

@Entity
@Table(name = "Variants")
@Data @AllArgsConstructor @NoArgsConstructor
public class Variant implements Serializable {

    @Id
    @Column(length = 13)
    private String variantID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productID")
    private Product product;

    @Column(length = 100, nullable = false)
    private String color;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean isMainVariant;

    @Column(nullable = false)
    private Boolean isUse;

    @OneToMany(mappedBy = "variant")
    private List<Image> images;

    @OneToMany(mappedBy = "variant")
    private List<Item> items;
}
