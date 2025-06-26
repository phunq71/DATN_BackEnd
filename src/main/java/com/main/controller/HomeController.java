package com.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
//	@GetMapping("/home")
//	public String home() {
//		return "View/Home";
//	}

//	@GetMapping("/header")
//	public String header() {
//		return "Layout/header";
//	}
//
//	@GetMapping("/footer")
//	public String footer() {
//		return "Layout/footer";
//	}

	@GetMapping("/index")
	public String index() {
		return "View/index";
	}

	@GetMapping("/viewAll")
	public String viewAll() {
		return "View/highlightProducts";
	}

	@GetMapping("/detail")
	public String detail() {
		return "View/productDetail";
	}

	@GetMapping("/cart")
	public String cart() {
		return "View/Cart";
	}
//
//	@GetMapping("/bottom-navigation")
//	public String bottomNavigation() {
//		return "Layout/bottom-navigation";
//	}
}
