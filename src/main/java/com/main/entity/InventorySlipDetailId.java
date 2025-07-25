package com.main.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySlipDetailId implements Serializable {
    private Integer itemId;
    private String inventorySlipId;
}

