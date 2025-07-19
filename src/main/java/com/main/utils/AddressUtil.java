package com.main.utils;

public final class AddressUtil {
    private AddressUtil() {
        // private constructor để không cho khởi tạo
    }

    /**
     * Tách địa chỉ thành mảng:
     * [0] = Số nhà, tên đường, tên nhà (nếu có)
     * [1] = Phường/Xã/Thị trấn
     * [2] = Quận/Huyện
     * [3] = Tỉnh/Thành phố
     */
    public static String[] splitAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isBlank()) {
            return new String[] { "", "", "", "" };
        }

        // Tách theo dấu phẩy
        String[] parts = fullAddress.split(",");

        String detail = parts.length > 0 ? parts[0].trim() : "";
        String ward = parts.length > 1 ? parts[1].trim() : "";
        String district = parts.length > 2 ? parts[2].trim() : "";
        String city = parts.length > 3 ? parts[3].trim() : "";

        return new String[] { detail, ward, district, city };
    }
}
