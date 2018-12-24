/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mykeycloak.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    private final static String EMAIL_PATTERN
            = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private final static String MOBILE_PATTERN
            = "(^00[1-9][0-9][1-9][0-9]{9,10}$|"
            + "^\\+[1-9][0-9][1-9][0-9]{9,10}$|"
            + "^3[0-9]{8,9}$)";
//            = "^(\\+|00|3)[1-9]+$";
    
    private final static String PASSWORD_PATTERN
            = "^[\\w\\W]{8,}$"; // lunghezza minima 8 caratteri

    public static boolean validateEmail(String email) {
        return validate(EMAIL_PATTERN, email);

    }

    public static boolean validateMobile(String mobile) {
        return validate(MOBILE_PATTERN, mobile);
    }
    
    public static boolean validatePassword(String password) {
        return validate(PASSWORD_PATTERN, password);
    }

    public static boolean validate(String pattern, String field) {
        Pattern _pattern = Pattern.compile(pattern);
        Matcher _matcher = _pattern.matcher(field);
        return _matcher.matches();
    }
    
    public static String normalizeMobile(String mobile) {
        String nmobile = mobile;
        if(mobile.startsWith("+")) {
            nmobile = mobile.replace("+", "00");
        } 
        if(mobile.startsWith("3")) {
            nmobile = "0039" + mobile;
        }
        return nmobile;
    }
}
