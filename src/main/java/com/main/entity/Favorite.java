package com.main.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "Favorites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite implements Serializable {

    @EmbeddedId
    private FavoriteId id;

    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "CustomerID")
    private Customer customer;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "ProductID")
    private Product product;
}
