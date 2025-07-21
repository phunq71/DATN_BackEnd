package com.main.service;

import com.main.dto.ProductViewDTO;
import com.main.dto.SupportDetailDTO;
import com.main.entity.Category;
import com.main.entity.Product;
import com.main.dto.ProductByCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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
    List<Product> findAll();
    Page<ProductViewDTO> filterProductsWithReviewOnly(
                                String color,
                                String brand,
                                BigDecimal priceFrom,
                                BigDecimal priceTo,
                                Double minRating,
                                String targetCustomer,
                                String categoryId,
                                String parentCategoryId,
                                Pageable pageable
    );
    List<ProductViewDTO> findTopFavorited();

    List<ProductViewDTO> searchProducts(String keyword);

}
