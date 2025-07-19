package com.main.serviceImpl;

import com.main.dto.Image_DetailDTO;
import com.main.entity.Variant;
import com.main.mapper.ImageMapper;
import com.main.repository.ImageRepository;
import com.main.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageRepository imageRepository;
    //Tìm danh sach img bằng variant
    public List<Image_DetailDTO> findByVariant(Variant variant){
        return imageRepository.findByVariant(variant)
                .stream()
                .map(ImageMapper::toDTO)
                .collect(Collectors.toList());
    }
}
