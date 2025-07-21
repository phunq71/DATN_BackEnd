package com.main.repository;

import com.main.entity.Favorite;
import com.main.entity.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository  extends JpaRepository<Favorite, FavoriteId> {
    //truy vấn sp dc yêu thích
    @Query("SELECT f.product.productID FROM Favorite f WHERE f.customer.customerId = :customerId")
    List<String> findProductIdsByCustomerId(@Param("customerId") String customerId);

    // Tìm bản ghi yêu thích theo mã khách hàng và mã sản phẩm
    Favorite findByCustomer_CustomerIdAndProduct_ProductID(String customerId, String productId);

    Integer countFavoriteByProduct_ProductID(String productId);

}
