package com.main.controller;

import com.main.dto.ProductFavoriteDTO;
import com.main.entity.Customer;
import com.main.repository.CustomerRepository;
import com.main.service.FavoriteService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/opulentia_user/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public String showFavoriteProducts(Model model) {
        String accountId = AuthUtil.getAccountID();
        List<ProductFavoriteDTO> favorites = favoriteService.getFavoritesByCustomer(accountId);

        model.addAttribute("products", favorites);


        return "View/favorite";
    }
}

