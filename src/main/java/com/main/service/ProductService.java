package com.main.service;

import com.main.dto.ProductViewDTO;
import com.main.dto.SupportDetailDTO;
import com.main.entity.Category;
import com.main.entity.Product;
import com.main.dto.ProductByCategoryDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> findByProductID(String id);
    List<Product> findByCategory(Category category);
    Page<ProductViewDTO> getProductsByCategory(String parentId, String childId, int page);
    List<ProductViewDTO> findProductsSale();
    List<ProductViewDTO> findProductNews();
    List<ProductViewDTO> findBestSellingProducts();
    void markFavorites(List<ProductViewDTO> products);
    SupportDetailDTO getSupportDetail(String id);
    //tìm kiếm sp theo tên
    List<ProductViewDTO> searchProducts(String keyword);

}
