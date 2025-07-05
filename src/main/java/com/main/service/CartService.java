package com.main.service;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;
import com.main.entity.CartId;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartService {
    public List<List<ItemCartDTO>> getItemCarts(List<CartDTO> carts);

    public List<MiniCartDTO> getMiniCarts(List<CartDTO> carts);

    public List<CartDTO> getCartsByCustomerId(String customerId);

    public List<List<ItemCartDTO>> updateCarts(List<CartDTO> carts);

    public List<CartDTO> addCustomerID(List<CartDTO> cart, String customerID);

    public void deleteCartsByCartIds(List<CartId> cartIds);

    public void clearCartsByCustomerId(String customerId);
}
