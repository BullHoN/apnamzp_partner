package com.avit.apnamzp_partner.models.payment;

public class OnlinePaymentOrderIdPostData {
    private int amount;
    private String shopId;

    public OnlinePaymentOrderIdPostData(int amount, String shopId) {
        this.amount = amount;
        this.shopId = shopId;
    }

    public String getShopId() {
        return shopId;
    }

    public int getAmount() {
        return amount;
    }
}
