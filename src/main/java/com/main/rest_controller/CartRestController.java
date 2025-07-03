package com.main.rest_controller;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;
import com.main.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/opulentia/rest/cart")
public class CartRestController {
    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }


    @PostMapping("/opulentia/rest/cart")
    public ResponseEntity<List<List<ItemCartDTO>>> cart(@RequestBody List<CartDTO> cartDTOs) {
        List<List<ItemCartDTO>> itemCartDTOs = new ArrayList<>();
        itemCartDTOs= cartService.getItemCarts(cartDTOs);
        System.out.println(itemCartDTOs);
        return ResponseEntity.ok(itemCartDTOs);
    }

    @PostMapping("/opulentia/rest/cart/miniCart")
    public ResponseEntity<List<MiniCartDTO>> miniCart(@RequestBody List<CartDTO> carts) {
        List<MiniCartDTO> miniCartDTOs = new ArrayList<>();
        miniCartDTOs= cartService.getMiniCarts(carts);
        return ResponseEntity.ok(miniCartDTOs);
    }

    @GetMapping("/opulentia/user/rest/cart")
    public ResponseEntity<List<List<ItemCartDTO>>> getCartsByUserId(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = principal.getName();
        System.out.println("username: "+username);
        List<CartDTO> carts = cartService.getCartsByCustomerId(username);
        System.out.println("carts: "+carts);
        List<List<ItemCartDTO>> itemCartDTOs = cartService.getItemCarts(carts);
        System.out.println("itemCartDTOs: "+itemCartDTOs);
        return ResponseEntity.ok(itemCartDTOs);
    }

}
