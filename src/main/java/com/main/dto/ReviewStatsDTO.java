package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//DTO lấy điểm trung bình toàn hệ thống và danh sách số lượng từng mức sao
public class ReviewStatsDTO {
    private double avgRating; // điểm trung bình toàn hệ thống
    private List<StarCountDTO> starCounts; // danh sách số lượng từng mức sao
}
