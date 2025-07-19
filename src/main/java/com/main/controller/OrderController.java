package com.main.controller;

import com.main.service.OrderService;
import com.main.utils.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderController {

    public final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/opulentia_user/allOrder")
    public String allOrder(Model model) {
        String accountId = AuthUtil.getAccountID();

        List<Integer> orderYears = orderService.getOrderYearByCustomerId(accountId);
        model.addAttribute("orderYears", orderYears);
        return "View/allOrders";
    }


    @GetMapping("/opulentia_user/orderDetail/*")
    public String orderDetails() {

        return "View/orderDetail";
    }
}
