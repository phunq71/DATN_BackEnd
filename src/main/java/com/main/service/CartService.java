package com.main.service;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;

import java.util.List;

public interface CartService {
    public List<List<ItemCartDTO>> getItemCarts(List<CartDTO> carts);

    public List<MiniCartDTO> getMiniCarts(List<CartDTO> carts);
}
