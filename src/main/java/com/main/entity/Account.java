package com.main.entity;

import jakarta.persistence.*;
import com.main.entity.Staff;
import com.main.entity.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "Accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    @Id
    @Column(name = "AccountID", length = 12)
    private String accountId;

    @Column(name = "Email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "Password", length = 50, nullable = false)
    private String password;

    @Column(name = "Status", nullable = false)
    private Boolean status;

    @Column(name = "CreateAt", nullable = false)
    private LocalDate createAt;

    @Column(name = "UpdateAt")
    private LocalDate updateAt;

    @OneToOne(mappedBy = "account")
    private Staff staff;

    @OneToOne(mappedBy = "account")
    private Customer customer;
}
