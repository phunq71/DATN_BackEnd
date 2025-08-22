package com.main.repository;

import com.main.dto.LogOrderDTO;
import com.main.entity.LogOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogOrderRepository extends JpaRepository<LogOrders, Integer> {

    @Query("""
        SELECT l.updateAt, l.content, staff.fullname
        FROM LogOrders l
        LEFT OUTER JOIN l.staff staff
        WHERE l.order.orderID=:orderId
""")
    List<LogOrderDTO> findByOrderId(@Param("orderId") Integer id);

}
