package com.avit.apnamzp_partner.models.offer;

public class OfferItem {
    private String _id;
    private String code;
    private String offerType;
    private Boolean isApnaMzpDiscount;
    private String shopName;
    private String shopId;
    private String discountAbove;

    // Type 1: "percent" Discount
    private String discountPercentage;
    private String maxDiscount;

    // Type 2: "flat" Discount
    private String discountAmount;

    public OfferItem(){

    }

    public OfferItem(String _id, String code, String offerType, Boolean isApnaMzpDiscount, String shopName, String shopId, String discountAbove, String discountPercentage, String maxDiscount, String discountAmount) {
        this._id = _id;
        this.code = code;
        this.offerType = offerType;
        this.isApnaMzpDiscount = isApnaMzpDiscount;
        this.shopName = shopName;
        this.shopId = shopId;
        this.discountAbove = discountAbove;
        this.discountPercentage = discountPercentage;
        this.maxDiscount = maxDiscount;
        this.discountAmount = discountAmount;
    }

    public OfferItem(String _id, String code, String offerType, Boolean isApnaMzpDiscount, String shopName, String discountAbove, String discountPercentage, String maxDiscount, String discountAmount) {
        this._id = _id;
        this.code = code;
        this.offerType = offerType;
        this.isApnaMzpDiscount = isApnaMzpDiscount;
        this.shopName = shopName;
        this.discountAbove = discountAbove;
        this.discountPercentage = discountPercentage;
        this.maxDiscount = maxDiscount;
        this.discountAmount = discountAmount;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public void setApnaMzpDiscount(Boolean apnaMzpDiscount) {
        isApnaMzpDiscount = apnaMzpDiscount;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setDiscountAbove(String discountAbove) {
        this.discountAbove = discountAbove;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public void setMaxDiscount(String maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String get_id() {
        return _id;
    }

    public String getCode() {
        return code;
    }

    public String getOfferType() {
        return offerType;
    }

    public Boolean getApnaMzpDiscount() {
        return isApnaMzpDiscount;
    }

    public String getShopName() {
        return shopName;
    }

    public String getDiscountAbove() {
        return discountAbove;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public String getMaxDiscount() {
        return maxDiscount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }
}
