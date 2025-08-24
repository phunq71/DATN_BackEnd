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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.FormattableFlags;
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
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductFavoriteDTO> getFavoritesByCustomer(String customerId) {
        // Lấy danh sách ID sản phẩm yêu thích của người dùng
        List<String> productIds = favoriteRepo.findProductIdsByCustomerId(customerId);

        // Lấy danh sách sản phẩm đầy đủ từ DB
        List<Product> products = productRepo.findAllById(productIds);
        Pageable pageable = PageRequest.of(0, 10);
        List <Product> products1 = productRepository.findTopSellingProducts(pageable);
        List<String> productIDs = products1.stream().map(Product::getProductID).toList();
        // Duyệt từng sản phẩm, map sang DTO
        List<ProductFavoriteDTO> listResult = new ArrayList<>();

        products.forEach(product -> {
            ProductFavoriteDTO productFavoriteDTO = new ProductFavoriteDTO();
            productFavoriteDTO = favoriteMapper.toDTO(product, productIDs);
            listResult.add(productFavoriteDTO);
        });

        return listResult;
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
