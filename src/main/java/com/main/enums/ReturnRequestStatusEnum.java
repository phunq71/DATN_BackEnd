package com.main.enums;

import lombok.Getter;

@Getter
public enum ReturnRequestStatusEnum {
    New("NEW", "Đã gửi yêu cầu");


    private final String dbValue;   // Lưu DB
    private final String displayName; // Hiện FE

    ReturnRequestStatusEnum(String dbValue, String displayName) {
        this.dbValue = dbValue;
        this.displayName = displayName;
    }

    // Optional: convert DB value => enum
    public static ReturnRequestStatusEnum fromDbValue(String dbValue) {
        for (ReturnRequestStatusEnum status : ReturnRequestStatusEnum.values()) {
            if (status.getDbValue().equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown dbValue: " + dbValue);
    }
}
