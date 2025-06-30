package com.main.service;

import com.main.entity.Cart;
import com.main.entity.CartId;

import java.util.List;

public interface CartService {
    public List<Cart> findAll();
    public Cart findById(CartId id);
    public CartId create(Cart cart);
    public Cart update(CartId id,Cart cart);
    public void deleteById(CartId id);
    public boolean existById(CartId id);
}
