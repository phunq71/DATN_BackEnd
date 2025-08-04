package com.main.utils;

import com.main.dto.StaffDTO;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$";
        return password != null && password.matches(regex);
    }

    public static boolean validateStaffDTO(StaffDTO staffDTO) {
        // Validate full name (letters only, no numbers/special chars except spaces and Vietnamese characters)
        if (staffDTO.getFullName() == null || staffDTO.getFullName().trim().isEmpty()) {
            return false;
        }
        if (!Pattern.matches("^[\\p{L} ]+$", staffDTO.getFullName())) {
            return false;
        }

        // Validate email format
        if (staffDTO.getEmail() == null || staffDTO.getEmail().trim().isEmpty()) {
            return false;
        }
        if (!Pattern.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", staffDTO.getEmail())) {
            return false;
        }

        // Validate password (only if not empty - for updates)
        if (staffDTO.getPassword() != null && !staffDTO.getPassword().isEmpty()) {
            if (!Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", staffDTO.getPassword())) {
                return false;
            }
        }

        // Validate phone number (start with 0, 10 digits)
        if (staffDTO.getPhone() == null || staffDTO.getPhone().trim().isEmpty()) {
            return false;
        }
        if (!Pattern.matches("^0\\d{9}$", staffDTO.getPhone())) {
            return false;
        }

        // Validate date of birth (not null and before today)
        if (staffDTO.getDob() == null || !staffDTO.getDob().isBefore(LocalDate.now())) {
            return false;
        }

        // Validate address detail (not empty and no commas)
        if (staffDTO.getAddressDetail() == null || staffDTO.getAddressDetail().trim().isEmpty()) {
            return false;
        }
        if (staffDTO.getAddressDetail().contains(",")) {
            return false;
        }

        // Validate role (not null and if STAFF, facilityID must be present)
        if (staffDTO.getRole() == null || staffDTO.getRole().trim().isEmpty()) {
            return false;
        }
//        if ("STAFF".equals(staffDTO.getRole()) && (staffDTO.getFacilityID() == null || staffDTO.getFacilityID().trim().isEmpty())) {
//            return false;
//        }

        // Status can be null (default to false)

        return true;
    }
}

