package com.main.rest_controller;

import com.main.dto.ProductFavoriteDTO;
import com.main.service.FavoriteService;
import com.main.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/opulentia_user/api/favorites")
public class FavoriteRestController {
    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<?> getFavorites() {
        if (!AuthUtil.isLogin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        String customerId = AuthUtil.getAccountID();

        List<ProductFavoriteDTO> favorites = favoriteService.getFavoritesByCustomer(customerId);

        return ResponseEntity.ok(favorites);
    }

    // Xóa sản phẩm yêu thích theo productId
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFavorite(@PathVariable("productId") String productId) {
        if (!AuthUtil.isLogin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        String accountId = AuthUtil.getAccountID();
        favoriteService.removeFavorite(accountId, productId);

        return ResponseEntity.ok().build(); // hoặc trả về message nếu muốn
    }

}
