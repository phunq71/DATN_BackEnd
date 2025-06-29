package com.main.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "sizes")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Size implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sizeID;

    @Column(length = 5)
    private String code;

    @OneToMany(mappedBy = "size")
    private List<Item> items;
}
