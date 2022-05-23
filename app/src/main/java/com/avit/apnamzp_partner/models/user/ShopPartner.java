package com.avit.apnamzp_partner.models.user;

public class ShopPartner {
    private String phoneNo;
    private String fcmId;
    private String shopId;
    private String shopType;

    public ShopPartner(String phoneNo) {
        this.phoneNo = phoneNo;
        this.fcmId = null;
    }

    public ShopPartner(String phoneNo, String shopId, String shopType) {
        this.phoneNo = phoneNo;
        this.shopId = shopId;
        this.shopType = shopType;
    }

    public String getShopType() {
        return shopType;
    }

    public String getShopId() {
        return shopId;
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
