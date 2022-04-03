package com.avit.apnamzp.models.shop;

import java.util.List;

public class ShopItemData {
    private String name;
    private String _id;
    private List<ShopPricingData> pricings;
    private String imageURL;
    public int quantity;
    public String taxOrPackigingPrice;
    public String discount;
    public Boolean available;
    private Boolean isVeg;

    public ShopItemData(String name, List<ShopPricingData> pricings, String imageURL) {
        this.name = name;
        this.pricings = pricings;
        this.imageURL = imageURL;
    }

    public Boolean getAvailable() {
        return available;
    }

    public Boolean getVeg() {
        return isVeg;
    }

    public String getDiscount() {
        return discount;
    }

    public String getTaxOrPackigingPrice() {
        return taxOrPackigingPrice;
    }

    public String get_id() {
        return _id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPricings(List<ShopPricingData> pricings) {
        this.pricings = pricings;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public List<ShopPricingData> getPricings() {
        return pricings;
    }
}
