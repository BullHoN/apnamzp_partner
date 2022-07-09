package com.avit.apnamzp_partner.models.user;

public class LoginPostData {
    private String phoneNo;
    private String password;

    public LoginPostData(String phoneNo, String password) {
        this.phoneNo = phoneNo;
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }
}
