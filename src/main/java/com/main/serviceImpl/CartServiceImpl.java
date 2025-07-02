package com.main.serviceImpl;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;
import com.main.repository.CartRepository;
import com.main.service.CartService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public List<List<ItemCartDTO>> getItemCarts(List<CartDTO> carts) {
        List<List<ItemCartDTO>> listItemCarts = new ArrayList<>();
        carts.forEach(cart -> {
            List<ItemCartDTO> itemCarts = new ArrayList<>();
            itemCarts=cartRepository.getItemsBySameProduct(cart.getItemID());
            itemCarts.forEach(itemCartDTO -> {
                if(itemCartDTO.getItemID().equals(cart.getItemID())){
                    itemCartDTO.setQuantity(cart.getQuantity());
                    itemCartDTO.setChosen(true);
                }
            });
            listItemCarts.add(itemCarts);

        });
        return listItemCarts;
    }

    @Override
    public List<MiniCartDTO> getMiniCarts(List<CartDTO> carts) {
        List<MiniCartDTO> miniCarts = new ArrayList<>();
        carts.forEach(cart -> {
            miniCarts.add(cartRepository.getMiniCarts(cart.getItemID()));
        });
        return miniCarts;
    }

}
