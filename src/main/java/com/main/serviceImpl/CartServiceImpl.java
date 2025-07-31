package com.main.serviceImpl;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;
import com.main.dto.OrderPreviewDTO;
import com.main.entity.Cart;
import com.main.entity.CartId;
import com.main.entity.Customer;
import com.main.entity.Item;
import com.main.mapper.CartMapper;
import com.main.repository.*;
import com.main.service.CartService;
import com.nimbusds.jose.util.Pair;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    public List<List<ItemCartDTO>> getItemCarts(List<CartDTO> carts) {
        List<List<ItemCartDTO>> listItemCarts = new ArrayList<>();
        carts.forEach(cart -> {
            List<ItemCartDTO> itemCarts = new ArrayList<>();
            itemCarts=cartRepository.getItemsBySameProduct(cart.getItemID());
            itemCarts.forEach(itemCartDTO -> {
                if(itemCartDTO.getItemID().equals(cart.getItemID())){ //cart chính
                    itemCartDTO.setQuantity(cart.getQuantity());
                    itemCartDTO.setChosen(true);
                    itemCartDTO.setLatestDate(cart.getLatestDate());
                    Item item= itemRepository.findById(itemCartDTO.getItemID()).get();
                    String productID = item.getVariant().getProduct().getProductID();
                    itemCartDTO.setDiscountPercent(productRepository.findDiscountPercentByProductID(productID));
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
    public List<List<ItemCartDTO>> updateCarts(List<CartDTO> carts, String customerId) {
        List<CartDTO> oldCarts = cartRepository.getCartsByCustomerId(customerId);
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

    @Override
    public List<CartDTO> mergeCarts(List<CartDTO> carts) {
        // Nhóm các cart item theo key (customerID + itemID) VÀ giữ thứ tự xuất hiện đầu tiên
        Map<String, List<CartDTO>> groupedCarts = carts.stream()
                .collect(Collectors.groupingBy(
                        cart -> cart.getCustomerID() + "-" + cart.getItemID(),
                        LinkedHashMap::new, // Giữ thứ tự
                        Collectors.toList()
                ));

        List<CartDTO> result = new ArrayList<>();

        for (Map.Entry<String, List<CartDTO>> entry : groupedCarts.entrySet()) {
            List<CartDTO> cartGroup = entry.getValue();

            if (cartGroup.size() == 1) {
                // Không trùng lặp
                result.add(cartGroup.get(0));
            } else {
                // Merge các cart cùng key
                CartDTO mergedCart = cartGroup.get(0);
                for (int i = 1; i < cartGroup.size(); i++) {
                    CartDTO current = cartGroup.get(i);
                    mergedCart.setQuantity(mergedCart.getQuantity() + current.getQuantity());
                    if (current.getLatestDate().isAfter(mergedCart.getLatestDate())) {
                        mergedCart.setLatestDate(current.getLatestDate());
                    }
                }

                // Kiểm tra tồn kho
                int stockQuantity = inventoryRepository.getStockQuantityByItemId(mergedCart.getItemID());
                if (mergedCart.getQuantity() > stockQuantity) {
                    mergedCart.setQuantity(stockQuantity);
                }

                result.add(mergedCart);
            }
        }

        return result;
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

    @Override
    public List<CartDTO> mergeCartLists(List<CartDTO> list1, List<CartDTO> list2) {
        // Map key là ItemID (Integer)
        Map<Integer, CartDTO> mergedMap = new HashMap<>();

        processCartList(mergedMap, list1, false);
        processCartList(mergedMap, list2, true);

        return new ArrayList<>(mergedMap.values());
    }

    @Override
    public List<CartDTO> newCarts(List<CartDTO> cartDTOs, String accountId) {
        Customer customer = customerRepository.findById(accountId).get();
        cartRepository.clearCartsByCustomerId(accountId);
        cartDTOs.forEach(cartDTO -> {
            Cart cart = new Cart();
            Item item= itemRepository.findById(cartDTO.getItemID()).get();
            CartId cartId = new CartId(cartDTO.getCustomerID(), cartDTO.getItemID());
            cart.setId(cartId);
            cart.setCustomer(customer);
            cart.setItem(item);
            cart.setQuantity(cartDTO.getQuantity());
            cart.setLatestDate(cartDTO.getLatestDate());
            cartRepository.save(cart);
        });

        return cartDTOs;
    }

    @Override
    public List<OrderPreviewDTO> getOrdersByItemIdsAndCustomerId(List<Integer> itemIds, String customerId) {
        List<OrderPreviewDTO> result = new ArrayList<>();
        for (Integer itemId : itemIds) {
            Cart cart = cartRepository.getCartByItem_itemIdAndCustomer_customerId(itemId, customerId);
            OrderPreviewDTO orderPreviewDTO = cartMapper.toOrderPreviewDTO(cart);
            result.add(orderPreviewDTO);
        }
        return result;
    }

    private void processCartList(Map<Integer, CartDTO> mergedMap, List<CartDTO> cartList, boolean isList2) {
        if (cartList == null) return;

        for (CartDTO cartItem : cartList) {
            // Key chỉ là ItemID
            Integer key = cartItem.getItemID();

            if (mergedMap.containsKey(key)) {
                // Đã tồn tại thì cộng quantity
                CartDTO existingItem = mergedMap.get(key);
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());

                // Nếu là list2 thì update CustomerID
                if (isList2) {
                    existingItem.setCustomerID(cartItem.getCustomerID());
                }

                // Cập nhật latestDate mới hơn
                if (cartItem.getLatestDate() != null &&
                        (existingItem.getLatestDate() == null ||
                                cartItem.getLatestDate().isAfter(existingItem.getLatestDate()))) {
                    existingItem.setLatestDate(cartItem.getLatestDate());
                }
            } else {
                // Thêm mới
                mergedMap.put(key, new CartDTO(
                        cartItem.getCustomerID(),
                        cartItem.getItemID(),
                        cartItem.getQuantity(),
                        cartItem.getLatestDate()
                ));
            }
        }
    }


}
