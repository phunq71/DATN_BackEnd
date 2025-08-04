package com.main.serviceImpl;

import com.main.entity.Item;
import com.main.entity.Variant;
import com.main.repository.ItemRepository;
import com.main.repository.SizeRepository;
import com.main.repository.VariantRepository;
import com.main.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private VariantRepository variantRepository;

    //Tim item bằng variant
    @Override
    public List<Item> findByVariant(Variant variant) {
        return itemRepository.findByVariant(variant);
    }

    @Override
    public void addItem(Integer selectedSizeId, List<String> selectedVariantIds) {
        selectedVariantIds.forEach(selectedVariantId -> {
            Item item = new Item();
            item.setSize(sizeRepository.findById(selectedSizeId).get());
            item.setVariant(variantRepository.findById(selectedVariantId).get());
            itemRepository.save(item);
        });
    }
}
