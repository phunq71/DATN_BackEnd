package com.main.serviceImpl;

import com.main.dto.ItemCartDTO;
import com.main.repository.CartRepository;
import com.main.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public List<ItemCartDTO> getItemCarts() {
        return null;
    }
}
