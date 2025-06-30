package com.main.service;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.entity.Cart;
import com.main.entity.CartId;

import java.util.List;

public interface CartService {
    public List<ItemCartDTO> getItemCarts(List<CartDTO> carts);
}
