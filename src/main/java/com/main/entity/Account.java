package com.main.entity;

import jakarta.persistence.*;
import com.main.entity.Staff;
import com.main.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "Accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    @Id
    @Column(name = "AccountID", length = 12)
    private String accountId;

    @Column(name = "Email", length = 50, nullable = false)
    private String email;

    @Column(name = "provider")
    private String provider; // "google", "facebook", ...

    @Column(name = "provider_id")
    private String providerId; // id duy nhất từ OAuth provider

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(length = 10, nullable = false)
    private String role;

    @Column(name = "Status", nullable = false)
    private Boolean status;

    @Column(name = "CreateAt", nullable = false)
    private LocalDate createAt;

    @Column(name = "UpdateAt")
    private LocalDate updateAt;

    @OneToOne(mappedBy = "account")
    private Staff staff;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Customer customer;
}
