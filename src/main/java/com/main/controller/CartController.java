package com.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/opulentia/cart")
public class CartController {
    @GetMapping
    public String cart(Principal principal) {
        if (principal != null) {
            return "View/cart-user";
        }
        return "View/cart-guest";
    }
}
