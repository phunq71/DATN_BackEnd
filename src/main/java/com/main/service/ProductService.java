package com.main.service;

import com.main.entity.Category;
import com.main.entity.Product;
import com.main.dto.ProductByCategory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> findByProductID(String id);
    List<Product> findByCategory(Category category);
    Page<ProductByCategory> getProductsByCategory(String parentId, String childId, int page);

}
