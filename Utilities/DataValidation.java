package Utilities;

import java.util.regex.Pattern;

public final class DataValidation {

    private DataValidation() {
        
    }

    public static boolean checkStringEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean checkStringLengthInRange(String value, int min, int max) {
        if (checkStringEmpty(value)) {
            return false;
        }
        int length = value.trim().length();
        return length >= min && length <= max;
    }

    public static boolean checkStringWithFormat(String value, String pattern) {
        if (value == null) {
            return false;
        }
        return Pattern.matches(pattern, value.trim());
    }

    public static boolean checkNumberInMinMax(int number, int min, int max) {
        return number >= min && number <= max;
    }

    public static boolean checkDoubleInMinMax(double number, double min, double max) {
        return number >= min && number <= max;
    }

    public static boolean isValidPhone(String phone) {
        return checkStringWithFormat(phone, "0\\d{9}");
    }

    public static boolean isValidEmail(String email) {
        return checkStringWithFormat(email, "^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidId(String id, String prefix) {
        return checkStringWithFormat(id, "^" + prefix + "\\d{3,}$");
    }
}
