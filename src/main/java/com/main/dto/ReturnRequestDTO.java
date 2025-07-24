package com.main.dto;

import com.main.enums.ReturnRequestStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReturnRequestDTO {
    private Integer returnRequestId;
    private LocalDateTime requestDate;
    private String status;
    private String statusName;
    private Integer orderId;
    List<ReturnItemDTO> items;
    private BigDecimal totalPrice;

    public ReturnRequestDTO(Integer returnRequestId, LocalDateTime requestDate, String status, Integer orderId) {
        this.returnRequestId = returnRequestId;
        this.requestDate = requestDate;
        this.status = status;
        ReturnRequestStatusEnum statusEnum = ReturnRequestStatusEnum.fromDbValue(status);
        this.statusName = statusEnum.getDisplayName();
        this.orderId = orderId;
    }

    public void setTotalPrice() {
        if (items == null || items.isEmpty()) {
            this.totalPrice = BigDecimal.ZERO;
            return;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ReturnItemDTO item : items) {
            if (item.getPrice() != null && item.getQuantity() != null) {
                BigDecimal itemTotal = item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                if (item.getDiscountPercent() != null && item.getDiscountPercent() > 0) {
                    BigDecimal discount = BigDecimal.valueOf(item.getDiscountPercent()).divide(BigDecimal.valueOf(100));
                    itemTotal = itemTotal.multiply(BigDecimal.ONE.subtract(discount));
                }

                total = total.add(itemTotal);
            }
        }

        this.totalPrice = total;
    }

}
