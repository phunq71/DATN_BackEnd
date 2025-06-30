package com.main.serviceImpl;

import com.main.dto.AvailableOptionDTO;
import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.SizeDTO;
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
    public List<ItemCartDTO> getItemCarts(List<CartDTO> carts) {
        List<ItemCartDTO> itemCarts = new ArrayList<>();
        carts.forEach(cart -> {
            ItemCartDTO itemCartDTO = cartRepository.getItemCart(cart.getItemID());
            itemCarts.add(itemCartDTO);
        });
        AvailableOptionDTO availableOptionDTO = new AvailableOptionDTO();
        itemCarts.forEach(cart -> {
            List<SizeDTO> sizeDTOs = new ArrayList<>();
           cartRepository.getSizeByVariantID(cart.getVariantID()).forEach(size -> {
               sizeDTOs.add(new SizeDTO(size.getSizeID(), size.getCode()));
           });
           availableOptionDTO.setColors(cartRepository.getColorByVariantID(cart.getVariantID()));
           availableOptionDTO.setSizes(sizeDTOs);
            cart.setAvailableOption(availableOptionDTO);
        });

        return itemCarts;
    }
}
