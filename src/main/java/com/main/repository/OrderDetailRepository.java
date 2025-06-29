package com.main.repository;

import com.main.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail, Integer> {
}
