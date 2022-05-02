package com.avit.apnamzp_partner.models.user;

public class ShopPartner {
    private String phoneNo;
    private String fcmId;

    public ShopPartner(String phoneNo) {
        this.phoneNo = phoneNo;
        this.fcmId = null;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getFcmId() {
        return fcmId;
    }
}
