package za.bc.cleaninginventory.util;

public class ValidationUtil {
    
    //Validation for the string length
    public static boolean isValidString(String str, int minLength, int maxLength){
        if (str == null) {
            return minLength == 0;
        }
        str = str.trim();
        return str.length() >= minLength && str.length() <= maxLength;
    }
    
    //Validation of positive numbers
    public static boolean isValidPositive(double value){
        return value > 0;
    }
    
    //Validation for positive or zero values
    public static boolean isValidPositiveOrZero(int value){
        return value >= 0;
    }
    
    //Validation for positive or zero values (for double)
    public static boolean isValidPositiveOrZero(double value){
        return value >= 0;
    }
    
    //Validate email format
    public static boolean isValidEmail(String email){
        if (email == null || email.trim().isEmpty()){
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    //Validate phone number to correct region (South African format)
    public static boolean isValidPhoneNumber(String phone){
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String phoneRegex = "^[0-9]{10}$";
        return phone.matches(phoneRegex);
    }
}