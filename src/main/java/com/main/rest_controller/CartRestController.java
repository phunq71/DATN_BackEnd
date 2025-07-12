package com.main.rest_controller;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;
import com.main.entity.CartId;
import com.main.security.CustomOAuth2User;
import com.main.security.CustomUserDetails;
import com.main.service.CartService;
import com.main.utils.AuthUtil;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/opulentia/rest/cart")
public class CartRestController {
    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }


    @PostMapping("/opulentia/rest/cart")
    public ResponseEntity<List<List<ItemCartDTO>>> cart(@RequestBody List<CartDTO> cartDTOs) {
       // System.out.println("✅ VÀO ĐƯỢC CONTROLLER /opulentia/rest/cart");
        List<List<ItemCartDTO>> itemCartDTOs = new ArrayList<>();
        cartDTOs = cartService.mergeCarts(cartDTOs);
        itemCartDTOs= cartService.getItemCarts(cartDTOs);
        System.out.println(itemCartDTOs);
        return ResponseEntity.ok(itemCartDTOs);
    }

    @PostMapping("/opulentia/rest/cart/miniCart")
    public ResponseEntity<List<MiniCartDTO>> miniCart(@RequestBody List<CartDTO> carts) {
       // System.out.println("✅ VÀO ĐƯỢC CONTROLLER /opulentia/rest/cart");
        List<MiniCartDTO> miniCartDTOs = new ArrayList<>();
        carts=cartService.mergeCarts(carts);
        miniCartDTOs= cartService.getMiniCarts(carts);
        return ResponseEntity.ok(miniCartDTOs);
    }

    @GetMapping("/opulentia_user/rest/cart")
    public ResponseEntity<List<List<ItemCartDTO>>> getCartsByUserId() {
        String accountId = AuthUtil.getAccountID();

        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("username: "+accountId);
        List<CartDTO> carts = cartService.getCartsByCustomerId(accountId);
        System.out.println("carts: "+carts);
        List<List<ItemCartDTO>> itemCartDTOs = cartService.getItemCarts(carts);
        System.out.println("itemCartDTOs: "+itemCartDTOs);
        return ResponseEntity.ok(itemCartDTOs);
    }

    /**
     * Kiểm tra danh sách cart nhận được nếu rỗng -> clear giỏ hàng
     * Nếu có cart, lấy nó so sánh với giỏ hàng trong DB
     *  - nếu có cart mới thì save
     *  - nếu cái cũ khác cái mới thì delete
     *  - do logic kiểm tra cũ mới nên sẽ dùng chung luôn cho trường hợp delete
     *
     * @param cartDTOs danh sách giỏ hàng từ client
     * @return danh sách các giỏ hàng cùng sản phẩm (bao gồm còn hàng và hết hàng)
     */
    @PutMapping("/opulentia_user/rest/cart/update")
    public ResponseEntity<List<List<ItemCartDTO>>> updateCart(@RequestBody List<CartDTO> cartDTOs) {
        String accountId = AuthUtil.getAccountID();

        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (cartDTOs.isEmpty()) {

            cartService.clearCartsByCustomerId(accountId);
            List<List<ItemCartDTO>> itemCartDTOs = new ArrayList<>();
            return ResponseEntity.ok(itemCartDTOs);
        }

        cartDTOs = cartService.mergeCarts(cartDTOs);
        List<List<ItemCartDTO>> itemCartDTOs = new ArrayList<>();
        List<CartDTO> carts= cartService.addCustomerID(cartDTOs, accountId);
        itemCartDTOs = cartService.updateCarts(carts, accountId);
        return ResponseEntity.ok(itemCartDTOs);
    }

    @PostMapping("/opulentia_user/mergeCartLocalStorageAndServer")
    public ResponseEntity<Boolean> mergeCartLocalStorageAndServer(@RequestBody List<CartDTO> cartDTOs) {
        String accountId = AuthUtil.getAccountID();

        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            System.err.println("cartDTOs: "+cartDTOs);
            List<CartDTO> currentCartDTOs = cartService.getCartsByCustomerId(accountId);
            System.err.println("currentCartDTOs: "+currentCartDTOs);
            List<CartDTO> newCartDTOs = cartService.mergeCartLists(cartDTOs, currentCartDTOs); //truyền current vào list2
            System.err.println("newCartDTOs: "+newCartDTOs);

            List<CartDTO> cartDTOList =cartService.newCarts(newCartDTOs, accountId);
            System.err.println("cartDTOList: "+cartDTOList);
            return ResponseEntity.ok(true);
        }catch(Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.ok(false);
        }
    }
}
