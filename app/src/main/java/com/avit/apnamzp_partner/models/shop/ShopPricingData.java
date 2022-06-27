package com.avit.apnamzp_partner.models.shop;

public class ShopPricingData {
    private String type;
    private String price;

    public ShopPricingData(String type, String price) {
        this.type = type;
        this.price = price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }
}
