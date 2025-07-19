package com.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {
    @GetMapping("/opulentia_user/review/{order}")
    public String review() {
        return "View/review";
    }
}
