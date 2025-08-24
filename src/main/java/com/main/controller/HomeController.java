package com.main.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.dto.ProductViewDTO;
import com.main.entity.Product;
import com.main.repository.OrderRepository;
import com.main.repository.ProductRepository;
import com.main.security.JwtService;
import com.main.service.ProductService;
import com.main.serviceImpl.ProductServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderRepository orderRepository;

//    @GetMapping({"/viewAll"})
//    public String viewAll() {
//        List<Product> products = productRepository.findByParentCategoryOnly2("A0001");
//        System.out.println(products.size());
//        return "View/highlightProducts";
//    }

    @GetMapping("/index")
    public String index(Model model) {

        List<ProductViewDTO> list = new ArrayList<>();
        list = productService.findProductsSale();

        List<ProductViewDTO> list2 = new ArrayList<>();
        list2 = productService.findProductNews();

        List<ProductViewDTO> list3 = new ArrayList<>();
        list3 = productService.findBestSellingProducts();

        List<ProductViewDTO> list4 = new ArrayList<>();
        list4 = productService.findTopFavorited();

        productService.markFavorites(list);
        productService.markFavorites(list2);
        productService.markFavorites(list3);
        productService.markFavorites(list4);


        model.addAttribute("discountedProducts", list);
        model.addAttribute("newProducts", list2);
        model.addAttribute("bestSellingProducts", list3);
        model.addAttribute("topFavoritedProducts", list4);

        return "View/index";
    }

    @GetMapping("/opulentia/deals")
    public String getDeals(Model model) {
        List<ProductViewDTO> list = new ArrayList<>();
        list = productService.findProductsSale();
        productService.markFavorites(list);
        model.addAttribute("products", list);
        model.addAttribute("title", "deals");
        return "View/Collection";
    }

    @GetMapping("/opulentia/news")
    public String getNews(Model model) {
        List<ProductViewDTO> list = new ArrayList<>();
        list = productService.findProductNews();
        productService.markFavorites(list);
        model.addAttribute("products", list);
        model.addAttribute("title", "news");
        return "View/Collection";
    }

    @GetMapping("/opulentia/bestselling")
    public String getBestSelling(Model model) {
        List<ProductViewDTO> list = new ArrayList<>();
        list = productService.findBestSellingProducts();
        productService.markFavorites(list);
        model.addAttribute("products", list);
        model.addAttribute("title", "bestSelling");
        return "View/Collection";
    }

    @GetMapping("/opulentia/topfavorite")
    public String getTopFavorite(Model model) {
        List<ProductViewDTO> list = new ArrayList<>();
        list = productService.findTopFavorited();
        productService.markFavorites(list);
        model.addAttribute("products", list);
        model.addAttribute("title", "topFavorite");
        return "View/Collection";
    }



    @GetMapping("/index2")
    public String index( Model model, HttpServletRequest request) {
        model.addAttribute("status", "success");
        model.addAttribute("messageLayout", "Đăng xuất thành công");
        return "View/index";
    }
//
//	@GetMapping("/viewAll")
//	public String viewAll() {
//		return "View/highlightProducts";
//	}
//
	@GetMapping("/detail")
	public String detail() {
		return "View/productDetail";
	}
//
//	@GetMapping("/cart")
//	public String cart() {
//		return "View/Cart";
//	}
//

//
	@GetMapping("/qrPay")
	public String qrPay() {
		return "View/qrPay";
	}
//
//	@GetMapping("/allOrders")
//	public String allOrders() {
//		return "View/allOrders";
//	}
    @GetMapping("/testqrcode")
    public String testqrcode() {
        return "temp/testQR";
    }

//
    @GetMapping("/opulentia_user/edit-profile")
    public String editProfile(HttpServletRequest request) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("🔐 AUTH tại /edit-profile: " + auth);
//        System.out.println("🔐 AUTHORITIES: " + auth.getAuthorities());
        return "View/edit-profile";
	}
	@GetMapping("/orderDetail")
	public String orderDetail() {
        return "View/orderDetail";
	}

    @GetMapping("/favicon.ico")
    public String redirectFavicon() {
        return "redirect:/logo/Opulentia.jpg";
    }



    @GetMapping("/opulentia_user/order_qrpay/qr")
    public String showQrPage(@RequestParam("maDH") String maDH,
                             Model model) {
        // Gán dữ liệu cho model để FE (Thymeleaf) render
        String formattedMaDH = String.format("#DH%06d", maDH);

        String numberPart = maDH.replace("#DH", "");

        Integer maDH1 = Integer.parseInt(numberPart);

        BigDecimal soTien = orderRepository.findById(maDH1).get().getTransaction().getAmount();

        model.addAttribute("maHD", formattedMaDH );
        model.addAttribute("soTien", soTien);


        return "View/qrPay"; // trỏ tới file qrPay.html
    }

//
//	@GetMapping("/return")
//	public String RProduct() {
//		return "View/ReturnProduct";
//	}
//
//	@GetMapping("/review")
//	public String review() {
//		return "View/review";
//	}
//	//
//	@GetMapping("/bottom-navigation")
//	public String bottomNavigation() {
//		return "Layout/bottom-navigation";
//	}
}
