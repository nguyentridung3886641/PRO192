package Utilities;

// Milestone 4 - Nguyen Tri Dung - Data Validation Utilities
import java.util.regex.Pattern;

public final class DataValidation {

    private DataValidation() {
    }

    public static boolean checkStringEmpty(String value) {
        return value == null || value.trim().isEmpty();
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

    public static boolean isValidPhone(String phone) {
        return checkStringWithFormat(phone, "\\d{9,11}");
    }

    public static boolean isValidId(String id, String prefix) {
        return checkStringWithFormat(id, "^" + prefix + "\\d{3,}$");
    }
}
