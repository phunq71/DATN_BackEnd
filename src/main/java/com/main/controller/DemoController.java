package com.main.controller;

import com.main.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DemoController {
    @GetMapping("/address")
    private String address() {
        return "/demo/DemoSeleboxAddress";
    }
    @Autowired
    private ImageRepository imageRepository;

//    @GetMapping("/testImg")
//    private String testImg(Model model) {
//        List<String> images = imageRepository.getImage();
//        model.addAttribute("images", images);
//        return "Hello";
//    }
}
