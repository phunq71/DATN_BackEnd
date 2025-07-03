package com.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/opulentia/cart")
public class CartController {
    @GetMapping
    public String cart() {
        return "View/cart";
    }
}
