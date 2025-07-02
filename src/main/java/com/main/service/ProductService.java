package com.main.service;

import com.main.dto.ProductByCategory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Page<ProductByCategory> getProductsByCategory(String parentId, String childId, int page);


}
