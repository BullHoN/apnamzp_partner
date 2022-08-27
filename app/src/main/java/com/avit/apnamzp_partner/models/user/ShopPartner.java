package com.avit.apnamzp_partner.models.user;

public class ShopPartner {
    private String phoneNo;
    private String fcmId;
    private String shopId;
    private String shopType;
    private String tagLine;
    private ShopPrices pricingDetails;
    private ShopAddressData addressData;
    private String bannerImage;
    private String taxPercentage;
    private String name;
    private boolean isOpen;
    private String shopItemsId;
    private String fssaiCode;

    public ShopPartner(String phoneNo, String fcmId, String shopId, String shopType, String tagLine, ShopPrices pricingDetails, ShopAddressData addressData, String bannerImage, String taxPercentage, String name, boolean isOpen, String shopItemsId, String fssaiCode) {
        this.phoneNo = phoneNo;
        this.fcmId = fcmId;
        this.shopId = shopId;
        this.shopType = shopType;
        this.tagLine = tagLine;
        this.pricingDetails = pricingDetails;
        this.addressData = addressData;
        this.bannerImage = bannerImage;
        this.taxPercentage = taxPercentage;
        this.name = name;
        this.isOpen = isOpen;
        this.shopItemsId = shopItemsId;
        this.fssaiCode = fssaiCode;
    }

    public ShopPartner(String phoneNo, String fcmId, String shopId, String shopType, String tagLine, ShopPrices pricingDetails, ShopAddressData addressData, String bannerImage, String taxPercentage, String name, boolean isOpen, String shopItemsId) {
        this.phoneNo = phoneNo;
        this.fcmId = fcmId;
        this.shopId = shopId;
        this.shopType = shopType;
        this.tagLine = tagLine;
        this.pricingDetails = pricingDetails;
        this.addressData = addressData;
        this.bannerImage = bannerImage;
        this.taxPercentage = taxPercentage;
        this.name = name;
        this.isOpen = isOpen;
        this.shopItemsId = shopItemsId;
    }

    public ShopPartner(String phoneNo, String fcmId, String shopId, String shopType, String tagLine, ShopPrices pricingDetails, ShopAddressData addressData, String bannerImage, String taxPercentage, String name, boolean isOpen) {
        this.phoneNo = phoneNo;
        this.fcmId = fcmId;
        this.shopId = shopId;
        this.shopType = shopType;
        this.tagLine = tagLine;
        this.pricingDetails = pricingDetails;
        this.addressData = addressData;
        this.bannerImage = bannerImage;
        this.taxPercentage = taxPercentage;
        this.name = name;
        this.isOpen = isOpen;
    }

    public ShopPartner(String phoneNo) {
        this.phoneNo = phoneNo;
        this.fcmId = null;
    }

    public ShopPartner(String phoneNo, String shopId, String shopType) {
        this.phoneNo = phoneNo;
        this.shopId = shopId;
        this.shopType = shopType;
    }



    public void setFssaiCode(String fssaiCode) {
        this.fssaiCode = fssaiCode;
    }

    public String getFssaiCode() {
        return fssaiCode;
    }

    public void setShopItemsId(String shopItemsId) {
        this.shopItemsId = shopItemsId;
    }

    public String getShopItemsId() {
        return shopItemsId;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getTagLine() {
        if(tagLine == null) return "";
        return tagLine;
    }

    public ShopPrices getPricingDetails() {
        return pricingDetails;
    }

    public ShopAddressData getAddressData() {
        return addressData;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public String getTaxPercentage() {
        if(taxPercentage == null) taxPercentage = "0";
        return taxPercentage;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public void setPricingDetails(ShopPrices pricingDetails) {
        this.pricingDetails = pricingDetails;
    }

    public void setAddressData(ShopAddressData addressData) {
        this.addressData = addressData;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
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
