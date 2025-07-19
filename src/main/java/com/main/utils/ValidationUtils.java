package com.main.utils;

public class ValidationUtils {
    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$";
        return password != null && password.matches(regex);
    }
}

