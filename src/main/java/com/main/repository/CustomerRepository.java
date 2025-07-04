package com.main.repository;

import com.main.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String > {
    @Query("SELECT MAX(c.customerId) FROM Customer c")
    String findMaxCustomerId();
}
