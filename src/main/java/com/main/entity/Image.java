package com.main.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "Images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageID")
    private Integer imageId;

    @ManyToOne
    @JoinColumn(name = "VariantID", referencedColumnName = "VariantID", insertable = false, updatable = false)
    private Variant variant;

    @Column(name = "ImageURL", nullable = false, length = 95)
    private String imageUrl;

    @Column(name = "IsMainImage", nullable = false)
    private Boolean isMainImage;
}
