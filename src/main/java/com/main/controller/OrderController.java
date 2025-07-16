package com.main.controller;

import com.main.utils.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {
    @GetMapping("/opulentia_user/allOrder")
    public String allOrder() {
        return "View/allOrders";
    }


    @GetMapping("/opulentia_user/orderDetail/*")
    public String orderDetails() {
        return "View/orderDetail";
    }
}
