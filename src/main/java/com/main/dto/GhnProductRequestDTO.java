package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GhnProductRequestDTO {
    private String name;
    private Integer quantity;
    private Integer price;
    private Integer height;
    private Integer weight;
    private Integer length;
    private Integer width;
}
