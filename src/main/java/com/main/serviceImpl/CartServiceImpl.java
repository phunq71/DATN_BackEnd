package com.main.serviceImpl;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;
import com.main.entity.Cart;
import com.main.entity.CartId;
import com.main.entity.Customer;
import com.main.entity.Item;
import com.main.repository.CartRepository;
import com.main.service.CartService;
import com.nimbusds.jose.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public List<List<ItemCartDTO>> getItemCarts(List<CartDTO> carts) {
        List<List<ItemCartDTO>> listItemCarts = new ArrayList<>();
        carts.forEach(cart -> {
            List<ItemCartDTO> itemCarts = new ArrayList<>();
            itemCarts=cartRepository.getItemsBySameProduct(cart.getItemID());
            itemCarts.forEach(itemCartDTO -> {
                if(itemCartDTO.getItemID().equals(cart.getItemID())){
                    itemCartDTO.setQuantity(cart.getQuantity());
                    itemCartDTO.setChosen(true);
                    itemCartDTO.setLatestDate(cart.getLatestDate());
                }
            });
            listItemCarts.add(itemCarts);

        });
        return listItemCarts;
    }

    @Override
    public List<MiniCartDTO> getMiniCarts(List<CartDTO> carts) {
        List<MiniCartDTO> miniCarts = new ArrayList<>();
        carts.forEach(cart -> {
            miniCarts.add(cartRepository.getMiniCarts(cart.getItemID()));
        });
        return miniCarts;
    }

    @Override
    public List<CartDTO> getCartsByCustomerId(String customerId) {
        return cartRepository.getCartsByCustomerId(customerId);
    }

    @Override
    public List<List<ItemCartDTO>> updateCarts(List<CartDTO> carts) {
        List<CartDTO> oldCarts = cartRepository.getCartsByCustomerId(carts.get(0).getCustomerID());
        List<CartDTO> saveCarts = getChanges(oldCarts, carts).getLeft();
        List<CartDTO> removeCarts = getChanges(oldCarts, carts).getRight();
        System.out.println("saveCarts: "+saveCarts);
        System.out.println("removeCarts: "+removeCarts);
        saveCarts.forEach(cartDTO -> {
            CartId cartId = new CartId(cartDTO.getCustomerID(), cartDTO.getItemID());
            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setItem(new Item(cartDTO.getItemID()));
            cart.setCustomer(new Customer(cartDTO.getCustomerID()));
            cart.setQuantity(cartDTO.getQuantity());
            cart.setLatestDate(LocalDateTime.now());
            cartRepository.save(cart);
        });

        if(!removeCarts.isEmpty()){
            removeCarts.forEach(cartDTO -> {
                CartId cartId = new CartId(cartDTO.getCustomerID(), cartDTO.getItemID());
                cartRepository.deleteById(cartId);
            });
        }
        return getItemCarts(carts);
    }

    @Override
    public List<CartDTO> addCustomerID(List<CartDTO> carts, String customerID) {
        carts.forEach(cart -> {
            cart.setCustomerID(customerID);
        });
        return carts;
    }

    @Override
    public void deleteCartsByCartIds(List<CartId> cartIds) {
        cartIds.forEach(cartRepository::deleteById);
    }

    @Override
    public void clearCartsByCustomerId(String customerId) {
        cartRepository.clearCartsByCustomerId(customerId);
    }


    /**
     * So sánh 2 list CartDTO (oldList và newList)
     * -> Trả về 1 Pair:
     *   - left: list cần save/update (thêm mới hoặc thay đổi)
     *   - right: list cần xóa
     *
     * @param oldList giỏ hàng cũ (trong DB)
     * @param newList giỏ hàng mới (từ client gửi lên)
     * @return Pair chứa 2 list: toSave, toDelete
     */
    private static Pair<List<CartDTO>, List<CartDTO>> getChanges(List<CartDTO> oldList, List<CartDTO> newList) {
        Map<String, CartDTO> oldMap = oldList.stream()
                .collect(Collectors.toMap(
                        cart -> cart.getCustomerID() + "-" + cart.getItemID(),
                        cart -> cart
                ));

        Map<String, CartDTO> newMap = newList.stream()
                .collect(Collectors.toMap(
                        cart -> cart.getCustomerID() + "-" + cart.getItemID(),
                        cart -> cart
                ));

        List<CartDTO> toDelete = new ArrayList<>();
        List<CartDTO> toSave = new ArrayList<>();

        // Tìm cần xóa
        for (String key : oldMap.keySet()) {
            if (!newMap.containsKey(key)) {
                toDelete.add(oldMap.get(key));
            }
        }

        // Tìm cần thêm / update
        for (String key : newMap.keySet()) {
            if (!oldMap.containsKey(key)) {
                // Mới thêm
                toSave.add(newMap.get(key));
            } else {
                CartDTO oldCart = oldMap.get(key);
                CartDTO newCart = newMap.get(key);

                if (oldCart.getQuantity() != newCart.getQuantity()
                        //  || !Objects.equals(oldCart.getLatestDate(), newCart.getLatestDate())
                ) {

                    // Bị thay đổi
                    toSave.add(newCart);
                }
            }
        }

        return Pair.of(toSave, toDelete);
    }
}
