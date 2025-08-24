package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionVoucherManagerDTO {
    private String promotionID;
    private String promotionName;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String banner;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String type;
    private String membershipId;
    private String rank;
}
