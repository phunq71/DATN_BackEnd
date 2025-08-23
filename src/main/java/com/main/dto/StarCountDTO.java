package com.main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//dto lấy số sao và số lượng đánh giá của mức sao đó
public class StarCountDTO {
    private int rating; // số sao (1-5)
    private long total; // số lượng đánh giá ở mức này
}
