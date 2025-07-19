package com.main.repository;

import com.main.entity.Item;
import com.main.entity.Product;
import com.main.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByVariant(Variant variant);
}
