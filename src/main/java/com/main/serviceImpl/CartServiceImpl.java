package com.main.serviceImpl;

import com.main.entity.Cart;
import com.main.entity.CartId;
import com.main.repository.CartRepository;
import com.main.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }


    @Override
    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public Cart findById(CartId id) {
        return cartRepository.findById(id).orElse(null);
    }

    @Override
    public CartId create(Cart cart) {
        return cartRepository.save(cart).getId();
    }

    @Override
    public Cart update(CartId id, Cart cart) {
        if(cartRepository.findById(id).isPresent()){
            cartRepository.save(cart);
            return cart;
        }
        return null;
    }

    @Override
    public void deleteById(CartId id) {
        cartRepository.deleteById(id);
    }

    @Override
    public boolean existById(CartId id) {
        return cartRepository.existsById(id);
    }
}
