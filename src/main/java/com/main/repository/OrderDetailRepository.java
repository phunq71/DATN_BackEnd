package com.main.repository;

import com.main.dto.OrderDTO;
import com.main.entity.OrderDetail;
import com.main.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail, Integer> {
    boolean existsByPromotionProduct(PromotionProduct promotionProduct);
}
