package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GhnOrderRequestDTO {
    private Integer orderID;
    private String to_name;
    private String to_phone;
    private String to_address;
    private String to_ward_code;
    private Integer to_district_code;

    private String from_ward_code;
    private Integer from_district_code;

    private String addressIDGHN_Shop; // Có thể null

    private BigDecimal cod_amount;
    private String content;

    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;

    private Integer payment_type_id;
    private String required_note;

    private Integer service_id;
    private Integer service_type_id;

    private List<GhnProductRequestDTO> items;
}
