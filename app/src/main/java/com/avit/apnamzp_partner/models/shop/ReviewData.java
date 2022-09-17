package com.avit.apnamzp_partner.models.shop;

public class ReviewData {

    private String userName;
    private String rating;
    private String userMessage;
    private String reviewType;
    private String shopName;
    private String orderId;

    public ReviewData(String userName, String rating, String userMessage, String reviewType, String shopName, String orderId) {
        this.userName = userName;
        this.rating = rating;
        this.userMessage = userMessage;
        this.reviewType = reviewType;
        this.shopName = shopName;
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getReviewType() {
        return reviewType;
    }

    public String getShopName() {
        return shopName;
    }

    public String getUserName() {
        return userName;
    }

    public String getRating() {
        return rating;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
