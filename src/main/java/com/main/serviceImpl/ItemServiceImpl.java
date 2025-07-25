package com.main.serviceImpl;

import com.main.entity.Item;
import com.main.entity.Variant;
import com.main.repository.ItemRepository;
import com.main.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;
    //Tim item bằng variant
    @Override
    public List<Item> findByVariant(Variant variant) {
        return itemRepository.findByVariant(variant);
    }
}
