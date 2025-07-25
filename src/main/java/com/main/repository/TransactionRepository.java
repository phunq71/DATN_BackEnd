package com.main.repository;

import com.main.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Transaction findByOrder_OrderID(Integer orderId);

}
