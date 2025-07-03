package com.main.rest_controller;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;
import com.main.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/opulentia/rest/cart")
public class CartRestController {
    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }


    @PostMapping
    public ResponseEntity<List<List<ItemCartDTO>>> cart(@RequestBody List<CartDTO> cartDTOs) {
        List<List<ItemCartDTO>> itemCartDTOs = new ArrayList<>();
        itemCartDTOs= cartService.getItemCarts(cartDTOs);
        System.out.println(itemCartDTOs);
        return ResponseEntity.ok(itemCartDTOs);
    }

    @PostMapping("/miniCart")
    public ResponseEntity<List<MiniCartDTO>> miniCart(@RequestBody List<CartDTO> carts) {
        List<MiniCartDTO> miniCartDTOs = new ArrayList<>();
        miniCartDTOs= cartService.getMiniCarts(carts);
        return ResponseEntity.ok(miniCartDTOs);
    }
}
