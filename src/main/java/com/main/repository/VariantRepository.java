package com.main.repository;

import com.main.entity.Product;
import com.main.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<Variant, String> {
    //Lấy list sp bang product
    List<Variant> findByProduct(Product product);
}
