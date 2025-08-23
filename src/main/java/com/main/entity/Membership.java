package com.main.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Memberships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Membership implements Serializable {

    @Id
    @Column(name = "MembershipID", length = 10)
    private String membershipId;

    @Column(name = "Rank", length = 25)
    private String rank;

    @Column(name = "Description", length = 750)
    private String description;

    @Column(name = "minPoint")
    private BigDecimal minPoint;

    @OneToMany(mappedBy = "membership")
    private List<Promotion> promotions;

    @OneToMany(mappedBy = "membership")
    private List<Customer> customers;


    public Membership(String membershipId, String rank, String description, BigDecimal minPoint) {
        this.membershipId = membershipId;
        this.rank = rank;
        this.description = description;
        this.minPoint = minPoint;
    }

}

