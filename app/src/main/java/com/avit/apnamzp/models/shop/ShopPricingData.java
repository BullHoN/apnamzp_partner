package com.avit.apnamzp.models.shop;

public class ShopPricingData {
    private String type;
    private String price;

    public ShopPricingData(String type, String price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }
}
