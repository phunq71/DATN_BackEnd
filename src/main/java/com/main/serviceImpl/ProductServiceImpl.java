package com.main.serviceImpl;

import com.main.entity.Category;
import com.main.entity.Product;
import com.main.repository.ProductRepository;
import com.main.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    //Tim sp cho trang chi tiet sp
    @Override
    public Optional<Product> findByProductID(String productID){
        return productRepository.findById(productID);
    }
    @Override
    public List<Product> findByCategory(Category category){
        return productRepository.findByCategory(category);
    }
}
