package util;

import config.AppConstants;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }


    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{" + AppConstants.PHONE_LENGTH + "}$");
    }


    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= AppConstants.MIN_USERNAME_LENGTH;
    }


    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= AppConstants.MIN_PASSWORD_LENGTH;
    }


    public static boolean isValidSeverity(int severity) {
        return severity >= AppConstants.MIN_SEVERITY && severity <= AppConstants.MAX_SEVERITY;
    }
}
