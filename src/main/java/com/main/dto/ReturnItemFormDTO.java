package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReturnItemFormDTO {
    private Integer orderDetailID;
    private Integer quantity;
    private String reason;
    private List<String> fileNames;
}
