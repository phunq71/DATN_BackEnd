package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class SupportDetailDTO {
    private Byte discountPercent; //phần trăm giảm gias
    private Boolean isFavorite; //Có yêu thích hay không
    private Boolean isHot; //Bán chạy
    private Boolean isNew; //Hàng mới
    private long soldQuantity; //số lượng đã bán
}
