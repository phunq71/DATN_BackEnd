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

	@GetMapping("/login")
	public String login() {
		return "Login/Dangky";
	}

	@GetMapping("/header")
	public String header() {
		return "Layout/header";
	}
	
	@GetMapping("/footer")
	public String footer() {
		return "Layout/footer";
	}
	
	@GetMapping("/index")
	public String index() {
		return "View/index";
	}

	@GetMapping("/bottom-navigation")
	public String bottomNavigation() {
		return "Layout/bottom-navigation";
	}
}
