package com.main.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Categories")
@Data
@ToString(exclude = {"parent", "childrens", "products"})
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    @Id
    @Column(name = "CategoryID", length = 5)
    private String categoryId;

    @Column(name = "CategoryName", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "Banner", length = 100)
    private String banner;

    @Column(name = "Content", length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParentID")
    private Category parent;  // danh mục cha

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> childrens;  // danh mục con

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;
}