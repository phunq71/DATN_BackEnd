package com.main.repository;

import com.main.dto.CustomerDTO;
import com.main.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String > {
    @Query("SELECT MAX(c.customerId) FROM Customer c")
    String findMaxCustomerId();

    Customer findByAccount_AccountId(String accountID);

    Customer findByCustomerId(String customerID);

    Customer findByQrToken(String qrToken);

    @Query("""
    SELECT c.customerId
         , c.fullName
         , c.gender
         , c.address
         , c.membership.rank
         , c.dob
         , c.imageAvt
         , c.addressIdGHN FROM Customer c
             WHERE c.customerId = :accountId
    """)
    CustomerDTO getCustomerByAccountID(@Param("accountId") String accountId);
}
