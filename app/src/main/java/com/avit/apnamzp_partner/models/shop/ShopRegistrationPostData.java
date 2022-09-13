package com.avit.apnamzp_partner.models.shop;

public class ShopRegistrationPostData {
    private String name;
    private String shopType;
    private String phoneNo;
    private String whatsappNumber;
    private String address;

    public ShopRegistrationPostData(){

    }

    public ShopRegistrationPostData(String name, String shopType, String phoneNo, String whatsappNumber) {
        this.name = name;
        this.shopType = shopType;
        this.phoneNo = phoneNo;
        this.whatsappNumber = whatsappNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getName() {
        return name;
    }

    public String getShopType() {
        return shopType;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }
}
