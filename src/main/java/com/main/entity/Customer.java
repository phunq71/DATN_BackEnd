package com.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable {

    @Id
    @Column(name = "CustomerID", length = 12)
    private String customerId;

    @Column(name = "FullName", nullable = false, length = 55)
    private String fullName;

    @Column(name = "Phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "Gender")
    private Boolean gender;

    @Column(name = "Address", nullable = false, length = 150)
    private String address;

    @Column(name = "AddressIdGHN", nullable = true, length = 100)
    private String addressIdGHN;

    @Column(name = "Dob")
    private LocalDate dob;

    @Column(name = "ImageAvt", nullable = false, length = 255)
    private String imageAvt;

    @ManyToOne
    @JoinColumn(name = "MembershipID", nullable = false)
    private Membership membership;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    @OneToMany(mappedBy = "customer")
    private List<Cart> carts;

    @OneToMany(mappedBy = "customer")
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "customer")
    private List<Review> reviews;

    @OneToOne
    @JoinColumn(name = "CustomerID")
    private Account account;

}
