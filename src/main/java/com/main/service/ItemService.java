package com.main.service;

import com.main.entity.Item;
import com.main.entity.Variant;

import java.util.List;

public interface ItemService {
    List<Item> findByVariant(Variant variant);
}
