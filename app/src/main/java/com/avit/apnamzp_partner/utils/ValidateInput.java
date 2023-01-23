package com.avit.apnamzp_partner.utils;

public class ValidateInput {
    public static boolean isValidPhoneNo(String phoneNo){
        return true;
    }

    public static boolean isValidPassword(String password){
        return true;
    }

    public static boolean isNumber(String str){
        if(str.length() == 0) return false;

        for(int i=0;i<str.length();i++){
            if(!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }


}
