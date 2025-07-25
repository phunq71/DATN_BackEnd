package com.main.serviceImpl;

import com.main.dto.ProductFavoriteDTO;
import com.main.entity.Customer;
import com.main.entity.Favorite;
import com.main.entity.FavoriteId;
import com.main.entity.Product;
import com.main.mapper.FavoriteMapper;
import com.main.repository.CustomerRepository;
import com.main.repository.FavoriteRepository;
import com.main.repository.ProductRepository;
import com.main.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepo;

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public List<ProductFavoriteDTO> getFavoritesByCustomer(String customerId) {
        // Lấy danh sách ID sản phẩm yêu thích của người dùng
        List<String> productIds = favoriteRepo.findProductIdsByCustomerId(customerId);

        // Lấy danh sách sản phẩm đầy đủ từ DB
        List<Product> products = productRepo.findAllById(productIds);

        // Duyệt từng sản phẩm, map sang DTO
        return products.stream()
                .map(favoriteMapper::toDTO)
                .collect(Collectors.toList());
    }

    //xóa sp dc yêu thích
    @Override
    public void removeFavorite(String accountId, String productId) {
        System.out.println("Xoá yêu thích: accountId = " + accountId + ", productId = " + productId);

        Customer customer = customerRepo.findByAccount_AccountId(accountId);
        System.out.println("Tìm được customer: " + (customer != null ? customer.getCustomerId() : "null"));

        if (customer == null) return;

        Favorite favorite = favoriteRepo.findByCustomer_CustomerIdAndProduct_ProductID(customer.getCustomerId(), productId);
        System.out.println("Tìm được favorite: " + (favorite != null ? "OK" : "null"));

        if (favorite != null) {
            favoriteRepo.delete(favorite);
        }
    }

    @Override
    public void addFavorite(String accountId, String productId) {
        Favorite favorite = new Favorite();
        favorite.setCustomer(customerRepo.findByAccount_AccountId(accountId));
        favorite.setProduct(productRepo.getReferenceById(productId));
        FavoriteId id = new FavoriteId();
        id.setProductId(productId);
        id.setCustomerId(accountId);
        favorite.setId(id);
        favoriteRepo.save(favorite);
        return;
    }


}
