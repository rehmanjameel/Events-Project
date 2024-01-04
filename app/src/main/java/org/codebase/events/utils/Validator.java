package org.codebase.events.utils;

import java.util.regex.Pattern;

public class Validator {

    public static boolean isValidMail(String email) {
        String EMAIL_STRING = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+)\\.[A-Za-z]{2,}$";
        return Pattern.compile(EMAIL_STRING).matcher(email).matches();
    }

//    public static boolean isValidMobile(String phone) {
//        String phoneValidation = "^(\\+91|0)?[6789]\\d{9}$";
////        String phoneValidation = "^((\\+92)|(0092))-{0,1}\\d{3}-{0,1}\\d{7}$|^\\d{11}$|^\\d{4}-\\d{7}$";
//        return Pattern.compile(phoneValidation).matcher(phone).matches();
//    }

    public static boolean isValidPakistanMobileNumber(String phoneNumber) {
        // Valid formats: +923001234567, 03001234567, 3001234567
        String PHONE_NUMBER_STRING = "^(\\+92|92|0)?[3456789]\\d{9}$";
        return Pattern.compile(PHONE_NUMBER_STRING).matcher(phoneNumber).matches();
    }

    public static boolean isValidPasswordFormat(String password) {
        String passwordREGEX = "^(?=.*[0-9])" + // at least 1 digit
                //                "(?=.*[a-z])" +         //at least 1 lower case letter
                //                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" + // any letter
                "(?=.*[@#$%^&+=])" + // at least 1 special character
                "(?=\\S+$)" + // no white spaces
                ".{6,}" + // at least 6 characters
                "$";
        return Pattern.compile(passwordREGEX).matcher(password).matches();
    }

    public static boolean isValidUserName(String userName) {
        String USER_NAME = "^(?=.*[a-zA-Z0-9])" + // any letter
                //                        "(?=.*[0-9])" +         //at least 1 digit
                // "(?=.*[a-z])" +         //at least 1 lower case letter
                // "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=\\S+$)" + // no white spaces
                // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                ".{4,}" + // at least 4 characters
                "$";
        return Pattern.compile(USER_NAME).matcher(userName).matches();
    }

    public static boolean isValidFirstLastName(String userName) {
        String USER_NAME = "^(?=.*[a-zA-Z0-9])" + // any letter
                //                    "(?=.*[0-9])" +         //at least 1 digit
                // "(?=.*[a-z])" +         //at least 1 lower case letter
                // "(?=.*[A-Z])" +         //at least 1 upper case letter
                ".{4,}" + // at least 4 characters
                // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                //                    "(?=\\S+$)" +           //no white spaces
                "$";
        return Pattern.compile(USER_NAME).matcher(userName).matches();
    }
}

