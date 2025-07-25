package com.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReturnRequestController {

    @GetMapping("/opulentia_user/returnRequest/*")
    public String returnRequest() {
        return "View/ReturnProduct";
    }

    @GetMapping("/opulentia_user/returnRequestDetail/{returnRequestID}")
    public String returnRequestDetail(@PathVariable String returnRequestID) {
        return "View/return-request-detail";
    }
}
