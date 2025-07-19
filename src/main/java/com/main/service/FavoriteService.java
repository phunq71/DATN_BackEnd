package com.main.service;

import com.main.dto.ProductFavoriteDTO;

import java.util.List;

public interface FavoriteService {
    List<ProductFavoriteDTO> getFavoritesByCustomer(String customerId);

    void removeFavorite(String accountId, String productId);

    void addFavorite(String accountId, String productId);

}
