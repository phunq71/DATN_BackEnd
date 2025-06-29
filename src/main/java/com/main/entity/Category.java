package com.main.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    @Id
    @Column(name = "CategoryID", length = 5)
    private String categoryId;

    @Column(name = "CategoryName", nullable = false, length = 100)
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "ParentID")
    private Category parent;  // danh mục cha

    @OneToMany(mappedBy = "parent")
    private List<Category> childrens;  // danh mục con

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}