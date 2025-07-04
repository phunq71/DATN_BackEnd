package com.main.repository;

import com.main.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail, Integer> {
    Optional<OrderDetail> findByOrderDetailID(Integer orderDetailID);
    @Query("""
    SELECT od
    FROM OrderDetail od
    JOIN od.item i
    JOIN i.variant v
    WHERE v.product.productID = :productID
    """)
    List<OrderDetail> findByProductId(@Param("productID") String productID);
}
