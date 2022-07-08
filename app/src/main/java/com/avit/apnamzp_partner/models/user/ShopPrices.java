package com.avit.apnamzp_partner.models.user;

public class ShopPrices {
    private String minOrderPrice;
    private String minFreeDeliveryPrice;

    public ShopPrices(String minOrderPrice, String minFreeDeliveryPrice) {
        this.minOrderPrice = minOrderPrice;
        this.minFreeDeliveryPrice = minFreeDeliveryPrice;
    }

    public String getMinOrderPrice() {
        if(minOrderPrice == null) return "";
        return minOrderPrice;
    }

    public String getMinFreeDeliveryPrice() {
        if(minFreeDeliveryPrice == null) return "";
        return minFreeDeliveryPrice;
    }
}
