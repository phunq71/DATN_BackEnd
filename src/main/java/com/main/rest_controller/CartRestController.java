package com.main.rest_controller;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.entity.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/opulentia/rest/cart")
public class CartRestController {
    @PostMapping
    public ResponseEntity<?> cart(@RequestBody List<CartDTO> cartDTOs) {
        cartDTOs.forEach(cart -> System.out.println(cart.getItemID()));
        return ResponseEntity.ok().build();
    }
}
