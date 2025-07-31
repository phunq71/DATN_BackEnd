package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityOrderDTO {
    // id cơ sở
    private String Id;

    // thời gian dự kiến giao sớm nhất
    private Instant leadtime;

    // số lượng đơn đang xử lý
    private long totalOrders;

    // địa chỉ
    private String address;

    // id địa chỉ
    private String addressIdGHN;
}
