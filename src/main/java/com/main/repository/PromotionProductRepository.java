package com.main.repository;

import com.main.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionProductRepository  extends JpaRepository<PromotionProduct, Integer> {
}
