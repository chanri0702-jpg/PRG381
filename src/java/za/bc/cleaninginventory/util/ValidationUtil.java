package za.bc.cleaninginventory.util;

import java.util.regex.Pattern;

/**
 * Utility class for user input validations.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Validation for string length
    public static boolean isValidString(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength == 0;
        }
        str = str.trim();
        return str.length() >= minLength && str.length() <= maxLength;
    }

    // Validation of positive numbers
    public static boolean isValidPositive(double value) {
        return value > 0;
    }

    // Validation for positive or zero values
    public static boolean isValidPositiveOrZero(int value) {
        return value >= 0;
    }

    // Validation for positive or zero values (double)
    public static boolean isValidPositiveOrZero(double value) {
        return value >= 0;
    }

    // Validate email format
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // Validate South African phone number
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return phone.matches("^[0-9]{10}$");
    }

    // Validate username (3–20 alphanumeric characters or underscore)
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    // Validate password (minimum 6 characters)
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= 6;
    }
}
