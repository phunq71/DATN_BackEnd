package com.main.repository;

import com.main.entity.Category;
import com.main.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository  extends JpaRepository<Product, String> {
    //Tìm sp bang id
    Optional<Product> findByProductID(String id);
    List<Product> findByCategory(Category category);
}
