package com.main.service;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;
import com.main.dto.OrderPreviewDTO;
import com.main.entity.CartId;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartService {
    public List<List<ItemCartDTO>> getItemCarts(List<CartDTO> carts);

    public List<MiniCartDTO> getMiniCarts(List<CartDTO> carts);

    public List<CartDTO> getCartsByCustomerId(String customerId);

    List<List<ItemCartDTO>> updateCarts(List<CartDTO> carts, String customerId);

    public List<CartDTO> addCustomerID(List<CartDTO> cart, String customerID);

    public void deleteCartsByCartIds(List<CartId> cartIds);

    public void clearCartsByCustomerId(String customerId);

    public List<CartDTO> mergeCarts(List<CartDTO> carts);

    List<CartDTO> mergeCartLists(List<CartDTO> list1, List<CartDTO> list2);

    public List<CartDTO> newCarts(List<CartDTO> carts, String accountId);

    public List<OrderPreviewDTO> getOrdersByItemIdsAndCustomerId(List<Integer> itemIds, String customerId);

}
