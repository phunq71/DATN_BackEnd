package com.main.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    Cho_xac_nhan("ChoXacNhan", "Chờ xác nhận"),
    Chuan_bi_don("ChuanBiDon", "Chuẩn bị đơn"),
    San_sang_giao("SanSangGiao", "Sẵn sàng giao"),
    Da_yeu_cau_huy("DaYeuCauHuy", "Đã yêu cầu hủy"),
    Cho_giao_hang("ChoGiaoHang","Chờ giao hàng"),
    Da_giao("DaGiao", "Đã giao"),
    YEU_CAU_HUY("YeuCauHuy", "Yêu cầu hủy"),
    Da_huy("DaHuy", "Đã hủy");


    private final String dbValue;   // Lưu DB
    private final String displayName; // Hiện FE

    OrderStatusEnum(String dbValue, String displayName) {
        this.dbValue = dbValue;
        this.displayName = displayName;
    }

    // Optional: convert DB value => enum
    public static OrderStatusEnum fromDbValue(String dbValue) {
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            if (status.getDbValue().equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown dbValue: " + dbValue);
    }
}
